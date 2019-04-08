/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.util;

import java.awt.Component;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Renderer for the file tree.
 *
 * @author Kirill Grouchnikov
 */
public class FileTreeCellRenderer extends DefaultTreeCellRenderer {

    /**
     * Icon cache to speed the rendering.
     */
    private Map<String, Icon> iconCache = new HashMap<String, Icon>();
    /**
     * Root name cache to speed the rendering.
     */
    private Map<File, String> rootNameCache = new HashMap<File, String>();
    protected static FileSystemView fsv = FileSystemView.getFileSystemView();

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree,
     *      java.lang.Object, boolean, boolean, boolean, int, boolean)
     */
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        FileTreeNode ftn = (FileTreeNode) value;
        File file = ftn.file;
        String filename = "";
        if (file != null) {
            if (ftn.isFileSystemRoot) {
                // long start = System.currentTimeMillis();
                filename = this.rootNameCache.get(file);
                if (filename == null) {
                    filename = fsv.getSystemDisplayName(file);
                    this.rootNameCache.put(file, filename);
                }
                // long end = System.currentTimeMillis();
                // System.out.println(filename + ":" + (end - start));
            } else {
                filename = file.getName();
            }
        }
        JLabel result = (JLabel) super.getTreeCellRendererComponent(tree,
                filename, sel, expanded, leaf, row, hasFocus);
        if (file != null) {
            Icon icon = this.iconCache.get(filename);
            if (icon == null) {
                // System.out.println("Getting icon of " + filename);
                icon = fsv.getSystemIcon(file);
                this.iconCache.put(filename, icon);
            }
            result.setIcon(icon);
        }
        return result;
    }
}
