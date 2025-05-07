package org.hospiconnect.model;

import java.util.Date;

public class Reclamation {

    // Attributs correspondant aux colonnes de la table
    private int User_id;
    private Long id;
    private String Title;
    private Long userId;
    private String description;
    private Date dateReclamation;
    private String status;
    private String category;
    private String priority;
    private Date resolutionDate;
    private String response;
    private Long resolvedBy;
    private Boolean isAnonymous;
    private String attachment;
    private String severity;
    private String resolvedByUsername;
    private String userName;

    // Constructeur par défaut
    public Reclamation() {
        this.Title="N/D";
        this.id = null;
        this.userId = null;
        this.description = "";
        this.dateReclamation = new Date();
        this.status = "En cours";
        this.category = "";
        this.priority = "";
        this.resolutionDate = null;
        this.response = "";
        this.resolvedBy = null;
        this.isAnonymous = false;
        this.attachment = "";
        this.severity = "";
        this.resolvedByUsername = "";
        this.userName = "";
    }

    //Construceur sans ID

    public Reclamation(String title, Long userId, String description, Date dateReclamation, String status, String category, Date resolutionDate, String priority, String response, Long resolvedBy, Boolean isAnonymous, String attachment, String severity) {
        Title = title;
        this.userId = userId;
        this.description = description;
        this.dateReclamation = dateReclamation;
        this.status = status;
        this.category = category;
        this.resolutionDate = resolutionDate;
        this.priority = priority;
        this.response = response;
        this.resolvedBy = resolvedBy;
        this.isAnonymous = isAnonymous;
        this.attachment = attachment;
        this.severity = severity;
    }

    //Constructeur avec tous les attributs
    public Reclamation(Long id, String title, Long userId, String description, Date dateReclamation, String status, String category, String priority, Date resolutionDate, String response, Long resolvedBy, Boolean isAnonymous, String attachment, String severity) {
        this.id = id;
        Title = title;
        this.userId = userId;
        this.description = description;
        this.dateReclamation = dateReclamation;
        this.status = status;
        this.category = category;
        this.priority = priority;
        this.resolutionDate = resolutionDate;
        this.response = response;
        this.resolvedBy = resolvedBy;
        this.isAnonymous = isAnonymous;
        this.attachment = attachment;
        this.severity = severity;
    }



    // Getters et Setters pour chaque attribut


    public int getUser_id() {
        return User_id;
    }

    public void setUser_id(int user_id) {
        User_id = user_id;
    }

    public String getTitle() {return Title;}
    public void setTitle(String title) {Title = title;}

    public Boolean getAnonymous() {return isAnonymous;}
    public void setAnonymous(Boolean anonymous) {isAnonymous = anonymous;}

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public Long getUserId() {return userId;}
    public void setUserId(Long userId) {this.userId = userId;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public Date getDateReclamation() {return dateReclamation;}
    public void setDateReclamation(Date dateReclamation) {this.dateReclamation = dateReclamation;}

    public String getStatus() {return status;}
    public void setStatus(String status) {this.status = status;}

    public String getCategory() {return category;}
    public void setCategory(String category) {this.category = category;}

    public String getPriority() {return priority;}
    public void setPriority(String priority) {this.priority = priority;}

    public Date getResolutionDate() {return resolutionDate;}
    public void setResolutionDate(Date resolutionDate) {this.resolutionDate = resolutionDate;}

    public String getResponse() {return response;}
    public void setResponse(String response) {this.response = response;}

    public Long getResolvedBy() {return resolvedBy;}
    public void setResolvedBy(Long resolvedBy) {this.resolvedBy = resolvedBy;}

    public Boolean getIsAnonymous() {return isAnonymous;}
    public void setIsAnonymous(Boolean isAnonymous) {this.isAnonymous = isAnonymous;}

    public String getAttachment() {return attachment;}
    public void setAttachment(String attachment) {this.attachment = attachment;}

    public String getSeverity() {return severity;}
    public void setSeverity(String severity) {this.severity = severity;}

    public String getResolvedByUsername() {return resolvedByUsername;}
    public void setResolvedByUsername(String resolvedByUsername) {this.resolvedByUsername = resolvedByUsername;}

    public String getUserName() {return userName;}
    public void setUserName(String userName) {this.userName = userName;}

    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + id +
                ", Title='" + Title + '\'' +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", description='" + description + '\'' +
                ", dateReclamation=" + dateReclamation +
                ", status='" + status + '\'' +
                ", category='" + category + '\'' +
                ", priority='" + priority + '\'' +
                ", resolutionDate=" + resolutionDate +
                ", response='" + response + '\'' +
                ", resolvedBy=" + resolvedBy +
                ", resolvedByUsername='" + resolvedByUsername + '\'' +
                ", isAnonymous=" + isAnonymous +
                ", attachment='" + attachment + '\'' +
                ", severity='" + severity + '\'' +
                '}';
    }
}
