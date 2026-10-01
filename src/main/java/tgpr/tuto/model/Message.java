package tgpr.tuto.model;

import org.springframework.util.Assert;
import tgpr.framework.mvc.Model;
import tgpr.framework.mvc.Params;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static tgpr.framework.util.Tools.asString;

public class Message extends Model {
    public enum Fields {
        PostId, Author, Recipient, Body, IsPrivate, DateTime
    }

    private int postId;
    private String authorPseudo;
    private String recipientPseudo;
    private String body;
    private boolean isPrivate;
    private LocalDateTime dateTime;

    public int getPostId() {
        return postId;
    }

    protected void setPostId(int postId) {
        this.postId = postId;
    }

    public String getAuthorPseudo() {
        return authorPseudo;
    }

    public void setAuthorPseudo(String authorPseudo) {
        this.authorPseudo = authorPseudo;
    }

    public String getRecipientPseudo() {
        return recipientPseudo;
    }

    public void setRecipientPseudo(String recipientPseudo) {
        this.recipientPseudo = recipientPseudo;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Member getAuthor() {
        return Member.getByPseudo(authorPseudo);
    }

    public Member getRecipient() {
        return Member.getByPseudo(recipientPseudo);
    }

    public Message() {
    }

    public Message(int postId, String authorPseudo, String recipientPseudo, String body, boolean isPrivate, LocalDateTime dateTime) {
        this.postId = postId;
        this.authorPseudo = authorPseudo;
        this.recipientPseudo = recipientPseudo;
        this.body = body;
        this.isPrivate = isPrivate;
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "Message{" +
                "postId=" + postId +
                ", author=" + authorPseudo +
                ", recipient=" + recipientPseudo +
                ", body='" + body + '\'' +
                ", private=" + isPrivate +
                ", dateTime=" + asString(dateTime) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return postId == message.postId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId);
    }

    protected void mapper(ResultSet rs) throws SQLException {
        postId = rs.getInt("post_id");
        authorPseudo = rs.getString("author");
        recipientPseudo = rs.getString("recipient");
        body = rs.getString("body");
        isPrivate = rs.getBoolean("private");
        dateTime = rs.getObject("date_time", LocalDateTime.class);
    }

    @Override
    public void reload() {
        reload("select * from messages where post_id=:postId", new Params("postId", postId));
    }

    public static Message getById(int id) {
        return queryOne(Message.class, "select * from messages where post_id=:postId", new Params("postId", id));
    }

    public static List<Message> getAll() {
        return queryList(Message.class, "select * from messages order by date_time");
    }

    public Message save() {
        int id = insert("insert into messages (author, recipient, body, private, date_time) values (:author,:recipient,:body,:isPrivate,:dateTime)",
                new Params("author", authorPseudo)
                        .add("recipient", recipientPseudo)
                        .add("body", body)
                        .add("isPrivate", isPrivate)
                        .add("dateTime", dateTime));
        if (id > 0)
            setPostId(id);
        Assert.isTrue(id > 0, "Something went wrong");
        return this;
    }

    public void delete() {
        Assert.isTrue(canDelete(Security.getLoggedUser()), "You're not allowed to delete this message");
        int c = execute("delete from messages where post_id=:postId", new Params("postId", postId));
        Assert.isTrue(c == 1, "Something went wrong");
    }

    public boolean canDelete(Member current) {
        return current.isAdmin() ||
                current.getPseudo().equals(authorPseudo) ||
                current.getPseudo().equals(recipientPseudo);
    }
}
