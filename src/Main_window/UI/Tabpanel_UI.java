package Main_window.UI;

import javafx.scene.control.Tab;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.View;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;

public class Tabpanel_UI extends BasicTabbedPaneUI
{
    private Color bg_color;
    private Color front_color;
    private Color selected_color;
    private Color deselected_color;
    private Color colorset_selected;
    private ColorSet selectedColorSet;
    private ColorSet hoverColorSet;
    private ColorSet defaultColorSet;
    private int anchoCarpetas = 18;

    public Tabpanel_UI(String bg_hex, String front_hex)
    {
        set_color(bg_hex, front_hex);

        selectedColorSet = new ColorSet();
        selectedColorSet.topGradColor1 = Color.decode("#E0EEEE");//选中的最上层
        selectedColorSet.topGradColor2 = Color.decode("#FFFFFF");//选中的第二层
        selectedColorSet.bottomGradColor1 = Color.decode("#FFFFFF");//选中的第三层
        selectedColorSet.bottomGradColor2 = Color.decode("#FFFFFF");//选中的最下层
        defaultColorSet = new ColorSet();
        defaultColorSet.topGradColor1 = Color.decode("#FFFFFF");//未选的最上层
        defaultColorSet.topGradColor2 = Color.decode("#DEEBFE");
        defaultColorSet.bottomGradColor1 = Color.decode("#D6E5F5");
        defaultColorSet.bottomGradColor2 = Color.decode("#D6E5F5");
        hoverColorSet = new ColorSet();
        hoverColorSet.topGradColor1 = Color.decode("#FFFFFF");//鼠标悬停最上层
        hoverColorSet.topGradColor2 = Color.decode("#FFFFFF");
        hoverColorSet.bottomGradColor1 = Color.decode("#FFFFFF");
        hoverColorSet.bottomGradColor2 = Color.decode("#FFFFFF");
    }

    public void set_color(String bg_hex, String front_hex)
    {
        bg_color = Color.decode(bg_hex);
        front_color = Color.decode(front_hex);
    }


    public static ComponentUI createUI(JComponent c)
    {
        return new Tabpanel_UI("#ffffff", "#000000");
    }

    @Override
    protected void installDefaults()
    {
        super.installDefaults();
        Rollover_listener listener = new Rollover_listener();
        tabPane.addMouseListener(listener);
        tabPane.addMouseMotionListener(listener);
        selected_color = new Color(250, 192, 192);
        deselected_color = new Color(197, 193, 168);
        tabAreaInsets.right = anchoCarpetas;
    }

    @Override
    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h)
    {
        super.paintContentBorderTopEdge(g, tabPlacement, -1, x, y, w, h);//不画上边框
    }



    /*@Override
    protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex)
    {
        if(runCount > 1)
        {
            int[] lines = new int[runCount];
            for(int i=0; i<runCount; i++)
            {
                lines[i] = rects[tabRuns[i]].y + (tabPlacement == TOP ? maxTabHeight : 0);
            }
            Arrays.sort(lines);
            if(tabPlacement == TOP)
            {
                int fila = runCount;
                for(int i=0; i<lines.length-1; i++)
                {
                    Polygon carp = new Polygon();
                    carp.addPoint(0, lines[i]);
                    carp.addPoint(tabPane.getWidth() - 2 * fila - 2, lines[i]);
                    carp.addPoint(tabPane.getWidth() - 2 * fila, lines[i] + 3);
                    if(i < lines.length - 2)
                    {
                        carp.addPoint(tabPane.getWidth() - 2 * fila,
                                lines[i + 1]);
                        carp.addPoint(0, lines[i + 1]);
                    }
                    else
                    {
                        carp.addPoint(tabPane.getWidth() - 2 * fila, lines[i]
                                + rects[selectedIndex].height);
                        carp.addPoint(0, lines[i] + rects[selectedIndex].height);
                    }
                    carp.addPoint(0, lines[i]);
                    g.setColor(hazAlfa(fila));
                    g.fillPolygon(carp);
                    g.setColor(darkShadow.darker());
                    g.drawPolygon(carp);
                }
            }
            else
            {
                int fila = 0;
                for (int i = 0; i < lines.length - 1; i++, fila++)
                {
                    Polygon carp = new Polygon();
                    carp.addPoint(0, lines[i]);
                    carp.addPoint(tabPane.getWidth() - 2 * fila - 1, lines[i]);
                    carp.addPoint(tabPane.getWidth() - 2 * fila - 1,
                            lines[i + 1] - 3);
                    carp.addPoint(tabPane.getWidth() - 2 * fila - 3,
                            lines[i + 1]);
                    carp.addPoint(0, lines[i + 1]);
                    carp.addPoint(0, lines[i]);
                    g.setColor(hazAlfa(fila + 2));
                    g.fillPolygon(carp);
                    g.setColor(darkShadow.darker());
                    g.drawPolygon(carp);
                }
            }
        }
    }*/

    protected Color hazAlfa(int fila)
    {
        int alfa = 0;
        if (fila >= 0)
        {
            alfa = 50 + (fila > 7 ? 70 : 10 * fila);
        }
        return new Color(0, 0, 0, alfa);
    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected)
    {
        Graphics2D g2D = (Graphics2D) g;
        ColorSet colorSet;
        Rectangle rect = rects[tabIndex];
        if (isSelected)
        {
            colorSet = selectedColorSet;
        }
        else if (getRolloverTab() == tabIndex)
        {
            colorSet = hoverColorSet;
        }
        else
        {
            colorSet = defaultColorSet;
        }
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        int width = rect.width;
        int xpos = rect.x;
        int yPos = rect.y;
        if (tabIndex > -1)
        {
            width--;
            xpos++;
            yPos += 2;
        }
        g2D.setPaint(new GradientPaint(xpos, 0, colorSet.topGradColor1, xpos,
                h / 2, colorSet.topGradColor2));
        g2D.fill(this.getUpArea(xpos, yPos, width, h - 2));

        g2D.setPaint(new GradientPaint(0, h / 2, colorSet.bottomGradColor1, 0,
                h, colorSet.bottomGradColor2));
        g2D.fill(this.getDownArea(xpos, yPos, width, h - 2));

    }

    private Shape getUpArea(int x, int y, int w, int h)
    {
        Rectangle2D rec = new Rectangle2D.Float(x, y, w, h / 3 );
        Area a = new Area(rec);
        RoundRectangle2D rect = new RoundRectangle2D.Float(x, y, w, h, 15, 15);
        Area b = new Area(rect);
        a.intersect(b);
        return a;
    }

    private Shape getDownArea(int x, int y, int w, int h)
    {
        Rectangle2D rec = new Rectangle2D.Float(x, y + h / 3, w, h*2 / 3 +1);
        Area a = new Area(rec);
        RoundRectangle2D rect = new RoundRectangle2D.Float(x, y, w, h, 15, 15);
        // Area b = new Area(rect);
        // a.intersect(b);
        return a;
    }

    protected void paintText(Graphics g, int tabPlacement, Font font,
                             FontMetrics metrics, int tabIndex, String title,
                             Rectangle textRect, boolean isSelected)
    {
        /*super.paintText(g, tabPlacement, font, metrics, tabIndex, title,
                textRect, isSelected);
        g.setFont(font);
        View v = getTextViewForTab(tabIndex);
        if (v != null)
        {
            // html
            v.paint(g, textRect);
        }
        else
        {
            // plain text
            int mnemIndex = tabPane.getDisplayedMnemonicIndexAt(tabIndex);
            if (tabPane.isEnabled() && tabPane.isEnabledAt(tabIndex))
            {
                g.setColor(tabPane.getForegroundAt(tabIndex));
                BasicGraphicsUtils
                        .drawStringUnderlineCharAt(g, title, mnemIndex,
                                textRect.x, textRect.y + metrics.getAscent());
            }
            else
            {// tab disabled
                g.setColor(Color.BLACK);
                BasicGraphicsUtils
                        .drawStringUnderlineCharAt(g, title, mnemIndex,
                                textRect.x, textRect.y + metrics.getAscent());
                g.setColor(tabPane.getBackgroundAt(tabIndex).darker());
                BasicGraphicsUtils.drawStringUnderlineCharAt(g, title,
                        mnemIndex, textRect.x - 1,
                        textRect.y + metrics.getAscent() - 1);
            }
        }*/
    }

    //固定tab标签宽度
    @Override
    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics)
    {
        return 120;
//      return 10 + anchoFocoV
//              + super.calculateTabWidth(tabPlacement, tabIndex, metrics);
    }

    @Override
    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight)
    {
        if (tabPlacement == LEFT || tabPlacement == RIGHT)
        {
            return super.calculateTabHeight(tabPlacement, tabIndex, fontHeight);
        }
        else
        {
            return 1 + super.calculateTabHeight(tabPlacement, tabIndex, fontHeight);
        }
    }

    protected int calculateMaxTabHeight(int tabPlacement)
    {
        return 25;
    }

    private int inclTab = 3;
    private Polygon shape;

    //set tab title hight
    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                  int x, int y, int w, int h, boolean isSelected)
    {
//      if (isSelected) {
        g.setColor(Color.decode("#afafaf"));
        int[] xp = new int[] { x, x, x + 3, x + w - inclTab - 6,
                x + w - inclTab - 3, x + w - inclTab, x + w - inclTab, x };
        int[] yp = new int[] { y + h, y + 3, y, y, y, y + 3, y + h, y + h };
        shape = new Polygon(xp, yp, xp.length);
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawPolygon(shape);
        g.setColor(Color.white);
        g.drawLine(x, y + h, x + w - inclTab, y + h);
//      }
    }

    protected int getTabLabelShiftY(int tabPlacement, int tabIndex, boolean isSelected)
    {
        return 0;
    }

    protected int getTabLabelShiftX(int tabPlacement, int tabIndex, boolean isSelected)
    {
        return 0;
    }

    private class ColorSet
    {
        Color topGradColor1;
        Color topGradColor2;
        Color bottomGradColor1;
        Color bottomGradColor2;
    }

    private int last_rollover = -1;

    private class Rollover_listener implements MouseListener, MouseMotionListener
    {
        private void check_roll()
        {
            int current = getRolloverTab();
            if(last_rollover != current)
            {
                last_rollover = current;
                Rectangle tabrec = new Rectangle(0, 0, tabPane.getWidth(), tabPane.getHeight() + 1);
                tabPane.repaint(tabrec);
            }
        }

        @Override
        public void mouseClicked(MouseEvent mouseEvent)
        {

        }

        @Override
        public void mousePressed(MouseEvent mouseEvent)
        {

        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent)
        {

        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent)
        {
            check_roll();
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent)
        {
            tabPane.repaint();
        }

        @Override
        public void mouseDragged(MouseEvent mouseEvent)
        {

        }

        @Override
        public void mouseMoved(MouseEvent mouseEvent)
        {
            check_roll();
        }
    }

}
