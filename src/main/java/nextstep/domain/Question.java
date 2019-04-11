package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable {
    @Size(min = 3, max = 100)
    @Column(length = 100, nullable = false)
    private String title;

    @Size(min = 3)
    @Lob
    private String contents;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
    private User writer;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    private boolean deleted = false;

    public Question() {
    }

    public Question(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public Question(long id, String title, String contents, User loginUser) {
        super(id);
        this.title = title;
        this.contents = contents;
        this.writer = loginUser;
    }

    public String getTitle() {
        return title;
    }

    public Question setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContents() {
        return contents;
    }

    public Question setContents(String contents) {
        this.contents = contents;
        return this;
    }


    public void writeBy(User loginUser) {
        this.writer = loginUser;
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Question update(User loginUser, Question targetQna) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }

        if(isDeleted()) {
            throw new IllegalArgumentException();
        }

        this.writer = loginUser;
        this.title = targetQna.title;
        this.contents = targetQna.contents;
        this.deleted = true;

        return this;
    }

    public void delete(User loginUser) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }

        if(isDeleted()) {
            throw new IllegalArgumentException();
        }

        this.deleted = true;
    }

    public Answer addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
        return answer;
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }
}
