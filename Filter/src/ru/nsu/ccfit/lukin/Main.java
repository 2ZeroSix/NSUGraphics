package ru.nsu.ccfit.lukin;

import ru.nsu.ccfit.lukin.model.filters.VRFilterLoader;
import ru.nsu.ccfit.lukin.model.filters.VolumeRenderingFilter;
import ru.nsu.ccfit.lukin.view.GUI;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        new GUI().setVisible(true);
//        VRFilterLoader loader = new VRFilterLoader(new VolumeRenderingFilter());
//        loader.load(new File("config.txt"));
    }
}
