package com.btzh.mytakephoto.moudle;
import com.jph.takephoto.model.TImage;

import java.io.Serializable;


/**
 * 对TImage的再次封装，
 */
public class MyTImage implements Serializable {

    TImage tImage;

    String localPath;  //本地路径

    String fileName;  //图片名称

    String type;  //图片类型

    public TImage gettImage() {
        return tImage;
    }

    public void settImage(TImage tImage) {
        this.tImage = tImage;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
