package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service("qnaService")
public class QnaService {
    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    public Question create(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Optional<Question> findById(long id) {
        return questionRepository.findById(id);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        Optional<Question> original = findById(id);
        return original.orElseThrow(IllegalArgumentException::new).update(loginUser, updatedQuestion);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Optional<Question> question = findById(questionId);
        question.orElseThrow(IllegalArgumentException::new).delete(loginUser);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Optional<Question> question = findById(questionId);
        return question.orElseThrow(IllegalAccessError::new).addAnswer(new Answer(loginUser, contents));
    }

    @Transactional
    public void deleteAnswer(User loginUser, long answerId) {
        Optional<Answer> answer = answerRepository.findById(answerId);
        answer.orElseThrow(IllegalArgumentException::new).deleteAnswer(loginUser);
    }
}
