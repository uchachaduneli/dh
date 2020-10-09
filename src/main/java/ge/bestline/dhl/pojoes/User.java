package ge.bestline.dhl.pojoes;

import java.io.Serializable;

/**
 * @author Ucha Chaduneli
 */
public class User implements Serializable {

    private int id;
    private String userName;
    private String password;
    private String description;
    private String typeName;
    private String invTypeName;
    private int statusId;
    private int typeId;
    private int invTypeId;
    private boolean editPass = false;
    private boolean inventarAccess = false;

    public String getInvTypeName() {
        return invTypeName;
    }

    public void setInvTypeName(String invTypeName) {
        this.invTypeName = invTypeName;
    }

    public int getInvTypeId() {
        return invTypeId;
    }

    public void setInvTypeId(int invTypeId) {
        this.invTypeId = invTypeId;
    }

    public boolean isInventarAccess() {
        return inventarAccess;
    }

    public void setInventarAccess(boolean inventarAccess) {
        this.inventarAccess = inventarAccess;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public boolean isEditPass() {
        return editPass;
    }

    public void setEditPass(boolean editPass) {
        this.editPass = editPass;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }
}
