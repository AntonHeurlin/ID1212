package common;

import java.io.Serializable;

public class FileDTO implements Serializable {

    public String filename;
    public String owner;
    public String size;

    public FileDTO(String filename, String owner, String size){
        this.filename = filename;
        this.owner = owner;
        this.size = size;
    }

    public String getFilename(){ return this.filename;}
    public String getOwner() { return this.owner;}
    public String getSize() { return this.size;}
}
