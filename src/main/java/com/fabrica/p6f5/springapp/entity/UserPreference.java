package com.fabrica.p6f5.springapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_preferences")
public class UserPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "preference_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "font_size")
    private String fontSize;

    @Column(name = "contrast_mode")
    private String contrastMode;

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getFontSize() { return fontSize; }
    public void setFontSize(String fontSize) { this.fontSize = fontSize; }
    public String getContrastMode() { return contrastMode; }
    public void setContrastMode(String contrastMode) { this.contrastMode = contrastMode; }
}



