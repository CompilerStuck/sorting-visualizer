package io.github.compilerstuck.Control;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import io.github.compilerstuck.SortingAlgorithms.*;
import io.github.compilerstuck.Visual.*;
import io.github.compilerstuck.Visual.Gradient.ColorGradient;
import processing.core.PApplet;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class Settings extends JFrame {

    protected PApplet proc;
    private int lastVisualIndex;

    JComboBox<String> gradientListComboBox;
    JSlider arraySizeSlider;
    private JPanel settingsPanel;
    private JPanel colorChoose1;
    private JPanel colorChoose2;
    private JCheckBox muteCheckBox;
    private JComboBox<String> algorithmListComboBox;
    private JLabel gradientListLabel;
    private JLabel algorithmListLabel;
    private JLabel arraySizeSliderLabel;
    private JLabel muteCheckBoxLabel;
    private JButton runButton;
    private JCheckBox runAllCheckBox;
    private JComboBox<String> shuffleListBox;
    private JLabel shuffleListLabel;
    private JButton cancelButton;
    private JCheckBox comparisonTableCheckBox;
    private JLabel arrayTitleLabel;
    private JLabel visualTitleLabel;
    private JProgressBar progressBarArray;
    private JLabel soundTitleLabel;
    private JLabel comparisonTableCheckBoxLabel;
    private JLabel sortingTitleLabel;
    private JComboBox<String> visualizationListComboBox;
    private JLabel visualizationListComboBoxLabel;
    private JTextField arraySizeTextField;
    private JButton arraySizeOkButton;
    private JCheckBox showMeasurementsCheckBox;
    private JLabel showMeasurementsLabel;

    ArrayList<SortingAlgorithm> algorithmList;
    ArrayList<ColorGradient> gradientList;
    ArrayList<ShuffleType> shuffleTypes;
    ArrayList<Visualization> visualizationList;


    public Settings() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        initialize();
    }

    public void initialize() {

        proc = MainController.processing;
        int maxSize = 5000;

        //Frame Settings
        setContentPane(settingsPanel);
        setSize(550, 530);
        setLocation(10, 10);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setTitle("Sorting Algorithm Visualizer - Settings");
        //setIconImage(new ImageIcon("src/main/resources/ChannelLogoWhite_64x64.png").getImage());

        int arraySize = MainController.getSize();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                MainController.shutdown();
            }
        });

        gradientList = new ArrayList<>(Arrays.asList(
                new ColorGradient(new Color(200, 0, 0), new Color(200, 0, 0), Color.WHITE, "Red"),
                new ColorGradient(new Color(0, 200, 0), new Color(0, 200, 0), Color.WHITE, "Green"),
                new ColorGradient(new Color(0, 0, 200), new Color(0, 0, 200), Color.WHITE, "Blue"),
                new ColorGradient(Color.WHITE, Color.WHITE, Color.RED, "White"),
                new ColorGradient(Color.WHITE, Color.BLACK, Color.WHITE, "White -> Black"),
                new ColorGradient(Color.RED, Color.BLACK, Color.WHITE, "Red -> Black"),
                new ColorGradient(Color.BLUE, Color.RED, Color.WHITE, "Blue -> Red"),
                new ColorGradient(Color.BLACK, Color.WHITE, Color.WHITE, "Black -> White"),
                new ColorGradient(Color.BLACK, Color.RED, Color.WHITE, "Black -> Red"),
                new ColorGradient(Color.PINK, Color.BLACK, Color.WHITE, "Custom Gradient")
        ));


        for (ColorGradient gradient : gradientList) {
            gradientListComboBox.addItem(gradient.getName());
        }

        gradientListComboBox.setSelectedIndex(8);
        Color color1 = MainController.getColorGradient().getMarkerColor(0, Marker.NORMAL);
        colorChoose1.setBackground(color1);
        Color color2 = MainController.getColorGradient().getMarkerColor(MainController.getSize() - 1, Marker.NORMAL);
        colorChoose2.setBackground(color2);
        colorChoose2.setVisible(true);

        gradientListComboBox.addActionListener(e -> {
            MainController.setColorGradient(gradientList.get(gradientListComboBox.getSelectedIndex()));
            Color newColor1 = MainController.getColorGradient().getMarkerColor(0, Marker.NORMAL);
            colorChoose1.setBackground(newColor1);
            Color newColor2 = MainController.getColorGradient().getMarkerColor(MainController.getSize() - 1, Marker.NORMAL);
            colorChoose2.setBackground(newColor2);
        });

        MouseListener ml = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JPanel jPanel = (JPanel) e.getSource();
                Color initColor = jPanel.getBackground();
                Color selectedColor = JColorChooser.showDialog(null,
                        "Color picker", jPanel.getBackground());
                if (selectedColor != null && !initColor.equals(selectedColor)) {
                    jPanel.setBackground(selectedColor);
                    if (jPanel.getName().equals("colorChoose1")) {
                        gradientList.get(gradientList.size() - 1).setColor1(selectedColor);
                        gradientList.get(gradientList.size() - 1).setColor2(colorChoose2.getBackground());
                    } else {
                        gradientList.get(gradientList.size() - 1).setColor2(selectedColor);
                        gradientList.get(gradientList.size() - 1).setColor1(colorChoose1.getBackground());
                    }
                    gradientListComboBox.setSelectedIndex(gradientList.size() - 1);
                }
            }
        };

        colorChoose1.addMouseListener(ml);
        colorChoose2.addMouseListener(ml);

        //Array size slider
        arraySizeSlider.setPaintTicks(true);
        arraySizeSlider.setPaintTrack(true);
        arraySizeSlider.setMinimum(0);
        arraySizeSlider.setMaximum(maxSize);
        arraySizeSlider.setValue(arraySize);
        arraySizeSlider.setMinorTickSpacing(maxSize / 8);
        arraySizeSlider.setMajorTickSpacing(maxSize / 4);
        arraySizeSlider.setPaintLabels(true);

        Color normalSliderColor = arraySizeSlider.getBackground();
        Color errorColor = new Color(255, 72, 72);
        Color normalTextFieldForegroundColor = arraySizeTextField.getForeground();

        arraySizeSlider.addChangeListener(e -> {
            if (arraySizeSlider.getValue() <= 3) {
                //arraySizeSlider.setValue(3);
                runButton.setEnabled(false);
                arraySizeSlider.setValue(3);
                arraySizeTextField.setText("3");
            } else {
                MainController.updateArraySize(arraySizeSlider.getValue());
                arraySizeTextField.setText(String.valueOf(arraySizeSlider.getValue()));
                runButton.setEnabled(true);
            }

            arraySizeOkButton.setEnabled(false);
        });

        arraySizeTextField.setText(String.valueOf(arraySizeSlider.getValue()));
        arraySizeTextField.addActionListener(e -> {
            if (arraySizeTextField.getText().equals(arraySizeTextField.getText().replaceAll("[^0-9]", "")) && arraySizeTextField.getText().length() < 5) {
                if (Integer.parseInt(arraySizeTextField.getText()) > maxSize) {
                    arraySizeSlider.setValue(maxSize);
                    arraySizeTextField.setText(String.valueOf(maxSize));
                } else {
                    arraySizeSlider.setValue(Integer.parseInt(arraySizeTextField.getText()));
                }

            }
            arraySizeOkButton.setEnabled(false);
        });

        arraySizeOkButton.setEnabled(false);
        arraySizeOkButton.addActionListener(e -> {
            if (arraySizeTextField.getText().equals(arraySizeTextField.getText().replaceAll("[^0-9]", "")) && arraySizeTextField.getText().length() < 5) {
                if (Integer.parseInt(arraySizeTextField.getText()) > maxSize) {
                    System.out.println("TEST ALLO");

                    arraySizeSlider.setValue(maxSize);
                    arraySizeTextField.setText(String.valueOf(maxSize));
                } else {
                    arraySizeSlider.setValue(Integer.parseInt(arraySizeTextField.getText()));
                }
            }
        });

        arraySizeTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            }

            public void removeUpdate(DocumentEvent e) {
                if (!arraySizeTextField.getText().equals(arraySizeTextField.getText().replaceAll("[^0-9]", ""))) {
                    arraySizeOkButton.setEnabled(false);
                    arraySizeTextField.setForeground(errorColor);
                } else {
                    arraySizeOkButton.setEnabled(true);
                    arraySizeTextField.setForeground(normalTextFieldForegroundColor);
                }
            }

            public void insertUpdate(DocumentEvent e) {
                if (!arraySizeTextField.getText().equals(arraySizeTextField.getText().replaceAll("[^0-9]", ""))) {
                    arraySizeOkButton.setEnabled(false);
                    arraySizeTextField.setForeground(errorColor);
                } else {
                    arraySizeOkButton.setEnabled(true);
                    arraySizeTextField.setForeground(normalTextFieldForegroundColor);
                }
            }
        });


        //Mute button
        muteCheckBox.setSelected(true);
        muteCheckBox.addChangeListener(e -> MainController.sound.setIsMuted(!muteCheckBox.isSelected()));


        //Algorithm selection //TODO -> READO ALGORITHMS
        algorithmList = new ArrayList<>(Arrays.asList(
                new QuickSortMiddlePivot(MainController.getArrayController()),
                new QuickSortDualPivot(MainController.getArrayController()),
                new MergeSort(MainController.getArrayController()),
                new ShellSort(MainController.getArrayController()),
                new GravitySort(MainController.getArrayController()),
                new RadixLSDSortBase10(MainController.getArrayController()),
                new GnomeSort(MainController.getArrayController()),
                new HeapSort(MainController.getArrayController()),
                new SelectionSort(MainController.getArrayController()),
                new CombSort(MainController.getArrayController()),
                new OddEvenSort(MainController.getArrayController()),
                new DoubleSelectionSort(MainController.getArrayController()),
                new InsertionSort(MainController.getArrayController()),
                new BubbleSort(MainController.getArrayController()),
                new CocktailSort(MainController.getArrayController()),
                new CycleSort(MainController.getArrayController()),
                //new BucketSort(MainController.getArrayController()),
                new CountingSort(MainController.getArrayController()),
                new AmericanFlagSort(MainController.getArrayController())
                //new BogoSort(MainController.getArrayController()),
                //new PigeonholeSort(MainController.getArrayController()),
                //new TimSort(MainController.getArrayController())
        ));

        for (SortingAlgorithm algorithm : algorithmList) {
            algorithmListComboBox.addItem(algorithm.getName());
        }
        algorithmListComboBox.setSelectedIndex(0);
        algorithmListComboBox.addActionListener(e -> MainController.setAlgorithm(algorithmList.get(algorithmListComboBox.getSelectedIndex())));

        //Run All Algorithms Checkbox
        runAllCheckBox.setSelected(false);
        runAllCheckBox.addActionListener(e -> {
            algorithmListComboBox.setEnabled(!runAllCheckBox.isSelected());
            if (runAllCheckBox.isSelected()) {
                MainController.setAlgorithms(algorithmList);
            } else {
                MainController.setAlgorithm(algorithmList.get(algorithmListComboBox.getSelectedIndex()));
            }
        });


        //Shuffle type
        shuffleTypes = new ArrayList<>(Arrays.asList(
                ShuffleType.RANDOM,
                ShuffleType.REVERSE,
                ShuffleType.ALMOST_SORTED,
                ShuffleType.SORTED
        ));

        for (ShuffleType shuffleType : shuffleTypes) {
            shuffleListBox.addItem(shuffleType.toString());
        }
        shuffleListBox.setSelectedIndex(0);
        shuffleListBox.addActionListener(e -> MainController.getArrayController().setShuffleType(shuffleTypes.get(shuffleListBox.getSelectedIndex())));


        //Run button
        runButton.addActionListener(e -> {
            MainController.setStart(true);
            cancelButton.setEnabled(true);
        });

        //Show measurements box
        showMeasurementsCheckBox.setSelected(true);
        showMeasurementsCheckBox.addActionListener(e -> {
            MainController.setPrintMeasurements(showMeasurementsCheckBox.isSelected());
        });

        //Show results Box
        comparisonTableCheckBox.addActionListener(e -> {
            MainController.setShowComparisonTable(comparisonTableCheckBox.isSelected());
            if (!MainController.isRunning() && cancelButton.isEnabled()) cancelButton.setEnabled(false);
        });


        //Progress bar
        progressBarArray.setMinimum(0);
        progressBarArray.setMaximum(100);
        progressBarArray.setValue(100);


        //Visual selection
        visualizationList = new ArrayList<>(Arrays.asList(
                new Classic(MainController.getArrayController(), MainController.getColorGradient(), MainController.getSound()),
                new ScatterPlot(MainController.getArrayController(), MainController.getColorGradient(), MainController.getSound()),
                new ScatterPlotLinked(MainController.getArrayController(), MainController.getColorGradient(), MainController.getSound()),
                new NumberPlot(MainController.getArrayController(), MainController.getColorGradient(), MainController.getSound()),
                new DisparityGraph(MainController.getArrayController(), MainController.getColorGradient(), MainController.getSound()),
                new DisparityGraphMirrored(MainController.getArrayController(), MainController.getColorGradient(), MainController.getSound()),
                new HorizontalPyramid(MainController.getArrayController(), MainController.getColorGradient(), MainController.getSound()),
                new ColorGradientGraph(MainController.getArrayController(), MainController.getColorGradient(), MainController.getSound()),
                new Circle(MainController.getArrayController(), MainController.getColorGradient(), MainController.getSound()),
                new DisparityCircle(MainController.getArrayController(), MainController.getColorGradient(), MainController.getSound()),
                new DisparityCircleScatter(MainController.getArrayController(), MainController.getColorGradient(), MainController.getSound()),
                new DisparityCircleScatterLinked(MainController.getArrayController(), MainController.getColorGradient(), MainController.getSound()),
                new SwirlDots(MainController.getArrayController(), MainController.getColorGradient(), MainController.getSound()),
                new Phyllotaxis(MainController.getArrayController(), MainController.getColorGradient(), MainController.getSound()), //behaving weird and kinda sucks
//                new ImageVertical(MainController.getArrayController(), MainController.getColorGradient(), MainController.getSound()),
//                new ImageHorizontal(MainController.getArrayController(), MainController.getColorGradient(), MainController.getSound()),
//                new MorphingShell(MainController.getArrayController(), MainController.getColorGradient(), MainController.getSound()),
                new Sphere(MainController.getArrayController(), MainController.getColorGradient(), MainController.getSound()),
                new Cube(MainController.getArrayController(), MainController.getColorGradient(), MainController.getSound())
        ));

        for (Visualization visualization : visualizationList) {
            visualizationListComboBox.addItem(visualization.getName());
        }

//        lastVisualIndex = 0;

        visualizationListComboBox.addActionListener(e -> {
            int index = visualizationListComboBox.getSelectedIndex();
//            if (index != 14 && index != 15) {
            MainController.setVisualization(visualizationList.get(index));
//                lastVisualIndex = index;
//            }else{                JFileChooser fileChooser = new JFileChooser();
//                            fileChooser.setDialogTitle("Select an image");
//                            fileChooser.setAcceptAllFileFilterUsed(false);
//                            FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG and JPG images", "png", "jpg");
//                            fileChooser.addChoosableFileFilter(filter);
//                            int fileChooserReturnValue = fileChooser.showDialog(null, "Select image");
//                            if (fileChooserReturnValue == JFileChooser.APPROVE_OPTION) {
//                                File selectedFile = fileChooser.getSelectedFile();
//                                String imagePath = selectedFile.getAbsolutePath();
//
//                                Visualization visualization = visualizationList.get(index);
//                                if (Objects.equals(visualization.getName(), "Image - Vertical Sorting")) {
//                                    ImageVertical imageVertical = (ImageVertical) visualization;
//                                    PImage pImage = proc.loadImage(imagePath);
//                                    imageVertical.setImg(pImage);
//                                    MainController.resizeScreen(pImage.pixelWidth, pImage.pixelHeight);
//                                    MainController.setVisualization(imageVertical);
//                                } else {
//                                    ImageHorizontal imageHorizontal = (ImageHorizontal) visualization;
//                                }
//
//                            } else {
//                                visualizationListComboBox.setSelectedIndex(lastVisualIndex);
//                            }
//            }

        });

        //Cancel button
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(e -> {
            SortingAlgorithm.setRun(false);
            cancelButton.setEnabled(false);
        });


        //Speed slider
        //TODO

        //Error dialogs:
//        JOptionPane.showMessageDialog(frame, "Eggs are not supposed to be green.");
        setVisible(true);
    }

    public void setEnableInputs(boolean enabled) {
        arraySizeSlider.setEnabled(enabled);
        algorithmListComboBox.setEnabled(enabled);
        runAllCheckBox.setEnabled(enabled);
        shuffleListBox.setEnabled(enabled);
        runButton.setEnabled(enabled);
        arraySizeOkButton.setEnabled(enabled);
        arraySizeTextField.setEnabled(enabled);
        visualizationListComboBox.setEnabled(enabled);
    }

    public void setEnableCancelButton(boolean enabled){
        cancelButton.setEnabled(enabled);
    }

    public void setProgressBar(int progress) {
        progressBarArray.setValue(progress);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridLayoutManager(21, 6, new Insets(0, 0, 0, 0), -1, -1));
        settingsPanel.setName("settingsPanel");
        settingsPanel.setOpaque(false);
        gradientListLabel = new JLabel();
        gradientListLabel.setText("Color gradient");
        settingsPanel.add(gradientListLabel, new GridConstraints(11, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        arrayTitleLabel = new JLabel();
        Font arrayTitleLabelFont = this.$$$getFont$$$(null, Font.BOLD, 16, arrayTitleLabel.getFont());
        if (arrayTitleLabelFont != null) arrayTitleLabel.setFont(arrayTitleLabelFont);
        arrayTitleLabel.setText("Array");
        settingsPanel.add(arrayTitleLabel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        arraySizeSliderLabel = new JLabel();
        arraySizeSliderLabel.setAlignmentX(0.0f);
        arraySizeSliderLabel.setFocusTraversalPolicyProvider(false);
        arraySizeSliderLabel.setHorizontalAlignment(0);
        arraySizeSliderLabel.setText("Array size");
        settingsPanel.add(arraySizeSliderLabel, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        algorithmListComboBox = new JComboBox();
        algorithmListComboBox.setMaximumRowCount(20);
        settingsPanel.add(algorithmListComboBox, new GridConstraints(6, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        gradientListComboBox = new JComboBox();
        gradientListComboBox.setMaximumRowCount(20);
        gradientListComboBox.setName("");
        settingsPanel.add(gradientListComboBox, new GridConstraints(11, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        algorithmListLabel = new JLabel();
        algorithmListLabel.setText("Sorting algorithm");
        settingsPanel.add(algorithmListLabel, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        shuffleListLabel = new JLabel();
        shuffleListLabel.setText("Shuffle type");
        settingsPanel.add(shuffleListLabel, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        shuffleListBox = new JComboBox();
        shuffleListBox.setMaximumRowCount(20);
        settingsPanel.add(shuffleListBox, new GridConstraints(7, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        settingsPanel.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        progressBarArray = new JProgressBar();
        settingsPanel.add(progressBarArray, new GridConstraints(20, 0, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        muteCheckBoxLabel = new JLabel();
        muteCheckBoxLabel.setText("Play sound");
        settingsPanel.add(muteCheckBoxLabel, new GridConstraints(16, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        muteCheckBox = new JCheckBox();
        muteCheckBox.setLabel("");
        muteCheckBox.setText("");
        settingsPanel.add(muteCheckBox, new GridConstraints(16, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        soundTitleLabel = new JLabel();
        Font soundTitleLabelFont = this.$$$getFont$$$(null, Font.BOLD, 16, soundTitleLabel.getFont());
        if (soundTitleLabelFont != null) soundTitleLabel.setFont(soundTitleLabelFont);
        soundTitleLabel.setText("Sound");
        settingsPanel.add(soundTitleLabel, new GridConstraints(15, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comparisonTableCheckBox = new JCheckBox();
        comparisonTableCheckBox.setText("");
        settingsPanel.add(comparisonTableCheckBox, new GridConstraints(12, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comparisonTableCheckBoxLabel = new JLabel();
        comparisonTableCheckBoxLabel.setText("Show comparison table");
        settingsPanel.add(comparisonTableCheckBoxLabel, new GridConstraints(12, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        settingsPanel.add(spacer2, new GridConstraints(14, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        runButton = new JButton();
        runButton.setText("run");
        settingsPanel.add(runButton, new GridConstraints(18, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cancelButton = new JButton();
        cancelButton.setText("cancel");
        settingsPanel.add(cancelButton, new GridConstraints(18, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        settingsPanel.add(spacer3, new GridConstraints(17, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        visualTitleLabel = new JLabel();
        Font visualTitleLabelFont = this.$$$getFont$$$(null, Font.BOLD, 16, visualTitleLabel.getFont());
        if (visualTitleLabelFont != null) visualTitleLabel.setFont(visualTitleLabelFont);
        visualTitleLabel.setText("Visual");
        settingsPanel.add(visualTitleLabel, new GridConstraints(9, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sortingTitleLabel = new JLabel();
        Font sortingTitleLabelFont = this.$$$getFont$$$(null, Font.BOLD, 16, sortingTitleLabel.getFont());
        if (sortingTitleLabelFont != null) sortingTitleLabel.setFont(sortingTitleLabelFont);
        sortingTitleLabel.setText("Sorting");
        settingsPanel.add(sortingTitleLabel, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        settingsPanel.add(spacer4, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        visualizationListComboBoxLabel = new JLabel();
        visualizationListComboBoxLabel.setText("Visualization");
        settingsPanel.add(visualizationListComboBoxLabel, new GridConstraints(10, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        visualizationListComboBox = new JComboBox();
        visualizationListComboBox.setMaximumRowCount(20);
        settingsPanel.add(visualizationListComboBox, new GridConstraints(10, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        settingsPanel.add(spacer5, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        arraySizeSlider = new JSlider();
        arraySizeSlider.setInverted(false);
        arraySizeSlider.setName("");
        arraySizeSlider.setOrientation(0);
        arraySizeSlider.setPaintLabels(true);
        arraySizeSlider.setPaintTicks(true);
        arraySizeSlider.setSnapToTicks(false);
        arraySizeSlider.setToolTipText("");
        arraySizeSlider.setValueIsAdjusting(false);
        arraySizeSlider.putClientProperty("JSlider.isFilled", Boolean.FALSE);
        arraySizeSlider.putClientProperty("Slider.paintThumbArrowShape", Boolean.FALSE);
        settingsPanel.add(arraySizeSlider, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        arraySizeTextField = new JTextField();
        settingsPanel.add(arraySizeTextField, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(20, -1), null, 0, false));
        showMeasurementsLabel = new JLabel();
        showMeasurementsLabel.setText("Show measurements");
        settingsPanel.add(showMeasurementsLabel, new GridConstraints(13, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showMeasurementsCheckBox = new JCheckBox();
        showMeasurementsCheckBox.setText("");
        settingsPanel.add(showMeasurementsCheckBox, new GridConstraints(13, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        settingsPanel.add(spacer6, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        arraySizeOkButton = new JButton();
        arraySizeOkButton.setText("ok");
        settingsPanel.add(arraySizeOkButton, new GridConstraints(3, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(50, -1), 0, false));
        runAllCheckBox = new JCheckBox();
        runAllCheckBox.setText("All");
        settingsPanel.add(runAllCheckBox, new GridConstraints(6, 4, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        settingsPanel.add(panel1, new GridConstraints(11, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        colorChoose1 = new JPanel();
        colorChoose1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        colorChoose1.setBackground(new Color(-47032));
        colorChoose1.setName("colorChoose1");
        panel1.add(colorChoose1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(10, 10), null, 0, false));
        colorChoose2 = new JPanel();
        colorChoose2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        colorChoose2.setBackground(new Color(-47032));
        colorChoose2.setDoubleBuffered(false);
        colorChoose2.setName("colorChoose2");
        panel1.add(colorChoose2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(10, 10), null, 0, false));
        final Spacer spacer7 = new Spacer();
        settingsPanel.add(spacer7, new GridConstraints(19, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer8 = new Spacer();
        settingsPanel.add(spacer8, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer9 = new Spacer();
        settingsPanel.add(spacer9, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return settingsPanel;
    }

}
