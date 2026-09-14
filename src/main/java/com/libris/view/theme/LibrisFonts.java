package com.libris.view.theme;

import java.awt.Font;

public final class LibrisFonts {
    // Serif Font for Academic Brand & Titles (Merriweather fallback to Georgia/Serif)
    public static final Font DISPLAY_LG  = new Font("Merriweather", Font.BOLD, 28);
    public static final Font DISPLAY_MD  = new Font("Merriweather", Font.BOLD, 22);
    public static final Font HEADLINE_LG = new Font("Merriweather", Font.BOLD, 18);
    public static final Font HEADLINE_MD = new Font("Merriweather", Font.BOLD, 16);

    // Sans-serif Font for Interface & Data (Inter / Segoe UI / SansSerif)
    public static final Font TITLE_LG    = new Font("Inter", Font.BOLD, 16);
    public static final Font TITLE_MD    = new Font("Inter", Font.BOLD, 14);
    public static final Font BODY_MD     = new Font("Inter", Font.PLAIN, 14);
    public static final Font BODY_SM     = new Font("Inter", Font.PLAIN, 12);
    public static final Font LABEL_MD    = new Font("Inter", Font.BOLD, 12);
    public static final Font LABEL_SM    = new Font("Inter", Font.BOLD, 11);

    // Monospaced Font for ISBN, Barcodes, Prices, IDs (JetBrains Mono / Monospaced)
    public static final Font CODE_MD     = new Font("JetBrains Mono", Font.PLAIN, 13);
    public static final Font CODE_SM     = new Font("JetBrains Mono", Font.PLAIN, 11);

    private LibrisFonts() {}
}

