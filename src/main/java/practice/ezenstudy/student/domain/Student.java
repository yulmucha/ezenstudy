package practice.ezenstudy.student.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import practice.ezenstudy.BaseEntity;
import practice.ezenstudy.SecurityUtils;

@Entity
public class Student extends BaseEntity {

    @Column(unique = true)
    private String email;

    private String nickname;

    @Column(nullable = false)
    private String password;

    protected Student() {
    }

    public Student(Long id, String email, String nickname, String password) {
        super(id);
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }

    public Student(String email, String nickname, String password) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }

    public Long getId() {
        return super.getId();
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    public boolean authenticate(String rawPassword) {
        String hashedInputPassword = SecurityUtils.sha256Encrypt(rawPassword);
        return this.password.equals(hashedInputPassword);
    }

    public void changePassword(String rawPassword) {
        this.password = SecurityUtils.sha256Encrypt(rawPassword);
    }
}
