package com.libris.utils;

import com.libris.model.ReportStat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ChartGenerator {

    public static JPanel createRevenueChart(List<ReportStat> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (ReportStat stat : data) {
            dataset.addValue(stat.getDoubleValue(), "Doanh thu", stat.getLabel());
        }
        
        JFreeChart chart = ChartFactory.createLineChart(
            "", 
            "Tháng / Năm",
            "Tổng doanh thu (VNĐ)",
            dataset,
            PlotOrientation.VERTICAL,
            false,
            true,
            false
        );

        chart.setBackgroundPaint(Color.WHITE);
        
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(226, 232, 240));
        plot.setDomainGridlinePaint(new Color(241, 245, 249));

        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(37, 99, 235)); 
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));  
        renderer.setDefaultShapesVisible(true);              

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(0, 260));
        chartPanel.setMouseWheelEnabled(true);
        return chartPanel;
    }
}