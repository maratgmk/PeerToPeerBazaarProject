package com.gafiev;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;


public class BellInequalityChart extends JFrame {
    public BellInequalityChart(String title)  {
        super(title);
        // Создание набора данных
        CategoryDataset dataset = createDataset();

        // Создание графика
        JFreeChart chart = ChartFactory.createBarChart(
                "Корреляции в эксперименте с неравенством Белла",
                "Измерения",
                "Корреляция E(Ai, Bj)",
                dataset
        );
        // Создание панели для отображения графика
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new java.awt.Dimension(800, 600));
        setContentPane(panel);
    }


    private CategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Корреляции
        dataset.addValue(0.85, "Корреляция", "E(A1, B1)");
        dataset.addValue(0.85, "Корреляция", "E(A1, B2)");
        dataset.addValue(0.85, "Корреляция", "E(A2, B1)");
        dataset.addValue(-0.85, "Корреляция", "E(A2, B2)");

        return dataset;
    }
}