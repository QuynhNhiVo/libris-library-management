package com.libris.utils;

import com.libris.model.ReportStat;
import com.libris.view.theme.LibrisColors;
import com.libris.view.theme.LibrisFonts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ChartGenerator {

    public static JPanel createRevenueChart(List<ReportStat> data) {
        return createRevenueComboChart(data);
    }

    public static JPanel createRevenueComboChart(List<ReportStat> data) {
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();

        if (data != null && !data.isEmpty()) {
            for (ReportStat stat : data) {
                barDataset.addValue(stat.getDoubleValue(), "Doanh thu (VNĐ)", stat.getLabel());
                lineDataset.addValue(stat.getValue() > 0 ? stat.getValue() : stat.getDoubleValue() / 10000.0, "Lượt mượn", stat.getLabel());
            }
        }

        CategoryPlot plot = new CategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(LibrisColors.HAIRLINE_BORDER);
        plot.setOutlineVisible(false);

        // Axis 1: Bar (Revenue)
        plot.setDataset(0, barDataset);
        BarRenderer barRenderer = new BarRenderer();
        barRenderer.setSeriesPaint(0, LibrisColors.PRIMARY);
        barRenderer.setBarPainter(new StandardBarPainter());
        barRenderer.setShadowVisible(false);
        plot.setRenderer(0, barRenderer);

        CategoryAxis domainAxis = new CategoryAxis();
        domainAxis.setTickLabelFont(LibrisFonts.BODY_SM);
        domainAxis.setAxisLinePaint(LibrisColors.HAIRLINE_BORDER);
        plot.setDomainAxis(domainAxis);

        NumberAxis rangeAxis1 = new NumberAxis("Doanh thu");
        rangeAxis1.setTickLabelFont(LibrisFonts.BODY_SM);
        rangeAxis1.setAxisLinePaint(LibrisColors.HAIRLINE_BORDER);
        plot.setRangeAxis(0, rangeAxis1);

        // Axis 2: Line (Rentals)
        plot.setDataset(1, lineDataset);
        LineAndShapeRenderer lineRenderer = new LineAndShapeRenderer();
        lineRenderer.setSeriesPaint(0, LibrisColors.ACCENT_BLUE);
        lineRenderer.setSeriesStroke(0, new BasicStroke(2.5f));
        lineRenderer.setDefaultShapesVisible(true);
        plot.setRenderer(1, lineRenderer);

        NumberAxis rangeAxis2 = new NumberAxis("Lượt mượn");
        rangeAxis2.setTickLabelFont(LibrisFonts.BODY_SM);
        plot.setRangeAxis(1, rangeAxis2);
        plot.mapDatasetToRangeAxis(1, 1);

        JFreeChart chart = new JFreeChart("", LibrisFonts.TITLE_MD, plot, true);
        chart.setBackgroundPaint(Color.WHITE);
        
        LegendTitle legend = chart.getLegend();
        if (legend != null) {
            legend.setFrame(org.jfree.chart.block.BlockBorder.NONE);
            legend.setItemFont(LibrisFonts.BODY_SM);
        }

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setPreferredSize(new Dimension(0, 260));
        return chartPanel;
    }

    public static JPanel createCategoryDonutChart(DefaultPieDataset dataset) {
        if (dataset == null || dataset.getItemCount() == 0) {
            dataset = new DefaultPieDataset();
        }

        JFreeChart chart = ChartFactory.createRingChart(
                "",
                dataset,
                true,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        RingPlot plot = (RingPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setSectionDepth(0.38);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} đơn ({2})"));
        plot.setLabelFont(LibrisFonts.BODY_SM);
        plot.setLabelBackgroundPaint(Color.WHITE);
        plot.setLabelOutlinePaint(LibrisColors.HAIRLINE_BORDER);

        // Color palette for slices
        Color[] colors = {
                LibrisColors.PRIMARY,
                LibrisColors.ACCENT_BLUE,
                LibrisColors.STATUS_SUCCESS,
                LibrisColors.SECONDARY,
                new Color(147, 51, 234),
                new Color(14, 165, 233)
        };

        for (int i = 0; i < dataset.getItemCount(); i++) {
            plot.setSectionPaint(dataset.getKey(i), colors[i % colors.length]);
        }

        LegendTitle legend = chart.getLegend();
        if (legend != null) {
            legend.setFrame(org.jfree.chart.block.BlockBorder.NONE);
            legend.setItemFont(LibrisFonts.BODY_SM);
        }

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setPreferredSize(new Dimension(0, 260));
        return chartPanel;
    }

    public static JPanel createCategoryDonutChartFromStats(java.util.List<com.libris.model.ReportStat> stats) {
        org.jfree.data.general.DefaultPieDataset dataset = new org.jfree.data.general.DefaultPieDataset();
        if (stats != null) {
            for (com.libris.model.ReportStat s : stats) {
                dataset.setValue(s.getLabel(), s.getValue());
            }
        }
        return createCategoryDonutChart(dataset);
    }

    public static JPanel createHorizontalCategoryBarChart(java.util.List<com.libris.model.ReportStat> stats, String xAxisLabel) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (stats != null) {
            for (com.libris.model.ReportStat s : stats) {
                double val = s.getValue() > 0 ? s.getValue() : s.getDoubleValue();
                dataset.addValue(val, xAxisLabel, s.getLabel());
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "", "", xAxisLabel,
                dataset, PlotOrientation.HORIZONTAL, false, true, false
        );

        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(LibrisColors.HAIRLINE_BORDER);
        plot.setOutlineVisible(false);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, LibrisColors.ACCENT_BLUE);
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setShadowVisible(false);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setPreferredSize(new Dimension(0, 240));
        return new ChartPanel(chart);
    }
}
