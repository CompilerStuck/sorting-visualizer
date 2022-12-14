package io.github.compilerstuck.SortingAlgorithms;

import io.github.compilerstuck.Control.ArrayController;
import io.github.compilerstuck.Control.MainController;
import io.github.compilerstuck.Visual.Marker;

import java.util.Random;

public class BubbleSort extends SortingAlgorithm {

    long startTime;

    public BubbleSort(ArrayController arrayController) {
        super(arrayController);
        this.name = "Bubble Sort";
        alternativeSize = arrayController.getLength();
    }


    public BubbleSort(ArrayController arrayController, int alternativeSize) {
        super(arrayController);
        this.name = "Bubble Sort";
        this.alternativeSize = alternativeSize;
    }

    public void sort() {
        MainController.setCurrentOperation(name);
        startTime = System.nanoTime();

        int n = arrayController.getLength();
        boolean swapped;
        do {
            swapped = false;
            for (int i = 0; i < n - 1 && run; ++i) {
                if (arrayController.get(i) > arrayController.get(i + 1)) {
                    arrayController.swap(i, i + 1);
                    swapped = true;
                    if (delay && new Random().nextInt(40) == 1) {
                        double test = new Random().nextInt(40);
                        arrayController.setMarker(i, Marker.SET);
                        arrayController.setMarker(i + 1, Marker.SET);
                        arrayController.addRealTime(System.nanoTime() - startTime);
                        proc.delay(1);
                        startTime = System.nanoTime();
                    }
                }
                arrayController.addComparisons(1);


            }


            n = n - 1;
        } while (swapped && run);


        arrayController.addRealTime(System.nanoTime() - startTime);

    }
}
