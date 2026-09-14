package com.libris.view.theme;

import java.awt.Color;

public final class LibrisColors {
    // Brand / Primary Colors
    public static final Color PRIMARY                = Color.decode("#0f172a"); // Slate 900
    public static final Color PRIMARY_HOVER          = Color.decode("#1e293b"); // Slate 800
    public static final Color PRIMARY_SUPPORTING     = Color.decode("#1e3a8a"); // Blue 900
    public static final Color ON_PRIMARY              = Color.decode("#ffffff");
    public static final Color PRIMARY_CONTAINER      = Color.decode("#0f172a");

    // Interactive Accent / Warm Amber
    public static final Color SECONDARY              = Color.decode("#d97706"); // Ochre Amber
    public static final Color SECONDARY_HOVER        = Color.decode("#b45309");
    public static final Color SECONDARY_SUPPORTING   = Color.decode("#f59e0b"); // Polished Brass
    public static final Color ACCENT_BLUE            = Color.decode("#2563eb"); // Royal Blue 600

    // Semantic Status Colors
    // Emerald — Available / Success
    public static final Color STATUS_SUCCESS         = Color.decode("#059669");
    public static final Color STATUS_SUCCESS_BG      = Color.decode("#ecfdf5");
    public static final Color STATUS_SUCCESS_BORDER  = Color.decode("#a7f3d0");
    public static final Color STATUS_SUCCESS_TEXT    = Color.decode("#047857");

    // Crimson — Overdue / Destructive / Rejected
    public static final Color STATUS_ERROR           = Color.decode("#dc2626");
    public static final Color STATUS_ERROR_BG        = Color.decode("#fef2f2");
    public static final Color STATUS_ERROR_BORDER    = Color.decode("#fecaca");
    public static final Color STATUS_ERROR_TEXT      = Color.decode("#b91c1c");

    public static final Color ERROR                  = STATUS_ERROR;
    public static final Color ERROR_CONTAINER         = STATUS_ERROR_BG;
    public static final Color ON_ERROR_CONTAINER      = STATUS_ERROR_TEXT;

    // Archival Warning — Pending / Active
    public static final Color STATUS_WARNING         = Color.decode("#d97706");
    public static final Color STATUS_WARNING_BG      = Color.decode("#fffbeb");
    public static final Color STATUS_WARNING_BORDER  = Color.decode("#fde68a");
    public static final Color STATUS_WARNING_TEXT    = Color.decode("#b45309");

    // Active / Renting (Blue)
    public static final Color STATUS_RENTED          = Color.decode("#3b82f6");
    public static final Color STATUS_RENTED_BG       = Color.decode("#eff6ff");
    public static final Color STATUS_RENTED_BORDER   = Color.decode("#bfdbfe");
    public static final Color STATUS_RENTED_TEXT     = Color.decode("#1d4ed8");

    // Surfaces & Canvas Backgrounds
    public static final Color CANVAS                 = Color.decode("#f8fafc");
    public static final Color SURFACE                = Color.decode("#f8fafc");
    public static final Color SURFACE_CONTAINER_LOWEST = Color.decode("#ffffff");
    public static final Color SURFACE_CONTAINER_LOW   = Color.decode("#eff4ff");
    public static final Color SURFACE_CONTAINER       = Color.decode("#f1f5f9");
    public static final Color SURFACE_CONTAINER_HIGH  = Color.decode("#e2e8f0");

    // Text & Borders
    public static final Color ON_SURFACE             = Color.decode("#0f172a");
    public static final Color ON_SURFACE_VARIANT     = Color.decode("#475569");
    public static final Color PLACEHOLDER            = Color.decode("#94a3b8");
    public static final Color OUTLINE                = Color.decode("#cbd5e1");
    public static final Color HAIRLINE_BORDER        = Color.decode("#e2e8f0");

    private LibrisColors() {}
}


