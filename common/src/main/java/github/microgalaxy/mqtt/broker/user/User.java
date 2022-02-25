package github.microgalaxy.mqtt.broker.user;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    @QuerySqlField
    private String username;
    private String password;
    private PermissionLevel level;
    private Boolean disable;
    private Date createTime;
    private transient String newPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public PermissionLevel getLevel() {
        return level;
    }

    public void setLevel(PermissionLevel level) {
        this.level = level;
    }

    public Boolean getDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
