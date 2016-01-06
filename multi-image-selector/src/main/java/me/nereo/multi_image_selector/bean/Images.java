package me.nereo.multi_image_selector.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/1/5.
 */
public class Images  implements Serializable {

    private List<Image>   images;

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
}
