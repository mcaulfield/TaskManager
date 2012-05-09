package com.aloe.mtm.gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/9/11
 * Time: 8:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageLoader {

    public static ImageIcon loadImage(String name, boolean large) {
        String filename1 = "img" + File.separator + name.toLowerCase() + ".png";
        String filename2 = "/img/" + name.toLowerCase() + ".png";
        ImageIcon icon = null;
        if (new File(filename1).exists()) {
            icon = new ImageIcon(filename1);
        } else {
            URL rsrc = ImageLoader.class.getResource(filename2);
            icon = new ImageIcon(rsrc);
        }
        if (icon == null) {
            return null;
        }
        int scale = large ? 32 : 16;
        Image img = icon.getImage().getScaledInstance(scale,scale,Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    public static ImageIcon loadImage(String name) {
        return loadImage(name, false);
    }

}
