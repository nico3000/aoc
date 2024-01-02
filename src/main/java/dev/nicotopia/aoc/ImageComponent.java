package dev.nicotopia.aoc;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JComponent;

import dev.nicotopia.Util;
import dev.nicotopia.aoc.algebra.Vec2i;

public class ImageComponent extends JComponent {
    private class ImageMouseListener extends MouseAdapter {
        private Vec2i lastCursorPos = null;
        private boolean pressed = false;

        public ImageMouseListener() {
            ImageComponent.this.addMouseListener(this);
            ImageComponent.this.addMouseMotionListener(this);
            ImageComponent.this.addMouseWheelListener(this);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (this.pressed) {
                Vec2i cursorPos = new Vec2i(e.getX(), e.getY());
                ImageComponent.this.pan(cursorPos.sub(this.lastCursorPos));
                this.lastCursorPos = cursorPos;
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            this.lastCursorPos = new Vec2i(e.getX(), e.getY());
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                this.pressed = true;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                this.pressed = false;
            }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            ImageComponent.this.scale(new Vec2i(e.getX(), e.getY()),
                    (float) Math.pow(1.1, -e.getPreciseWheelRotation()));
        }
    }

    private final Image image;
    private Vec2i offset = new Vec2i(0, 0);
    private float scaling = 1.0f;

    public ImageComponent(Image image) {
        this.image = image;
        this.setPreferredSize(new Dimension(1280, 720));
        new ImageMouseListener();
        float widthScaling = 1280.0f / (float) image.getWidth(this);
        this.scaling = Util.max(widthScaling, 1.0f);
    }

    @Override
    public void paint(Graphics g) {
        int w = (int) Math.ceil((float) this.image.getWidth(this) * this.scaling);
        int h = (int) Math.ceil((float) this.image.getHeight(this) * this.scaling);
        int x = Util.clamp(this.offset.x(), -w + 8, this.getWidth() - 8);
        int y = Util.clamp(this.offset.y(), -h + 8, this.getHeight() - 8);
        g.drawImage(this.image, x, y, w, h, this);
    }

    private void scale(Vec2i fixPoint, float factor) {
        float newScaling = this.scaling * factor;
        this.offset = fixPoint.add(this.offset.sub(fixPoint).mulInt(newScaling / scaling));
        this.scaling = newScaling;
        this.repaint();
    }

    private void pan(Vec2i delta) {
        this.offset = this.offset.add(delta);
        this.repaint();
    }
}
