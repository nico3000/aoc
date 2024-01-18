package dev.nicotopia.aoc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.swing.JComponent;

import dev.nicotopia.Util;
import dev.nicotopia.aoc.algebra.Vec2d;
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
                ImageComponent.this.offset = ImageComponent.this.getClampedOffset();
            }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            ImageComponent.this.scale(new Vec2i(e.getX(), e.getY()),
                    (float) Math.pow(1.1, -e.getPreciseWheelRotation()));
        }
    }

    public static <E> BufferedImage imageFrom(E[][] imageData, Function<E, Color> color) {
        if (Arrays.stream(imageData).mapToInt(r -> r.length).distinct().count() != 1) {
            throw new AocException("Image data must be rectangular.");
        }
        return ImageComponent.imageFrom(imageData[0].length, imageData.length, (x, y) -> color.apply(imageData[y][x]));
    }

    public static <E> BufferedImage imageFrom(int[][] imageData, Function<Integer, Color> color) {
        if (Arrays.stream(imageData).mapToInt(r -> r.length).distinct().count() != 1) {
            throw new AocException("Image data must be rectangular.");
        }
        return ImageComponent.imageFrom(imageData[0].length, imageData.length, (x, y) -> color.apply(imageData[y][x]));
    }

    public static <E> BufferedImage imageFrom(E[][] imageData) {
        List<Integer> palette = new LinkedList<>(Arrays.asList(0x222222, 0xffffff, 0x4b4e6d, 0x84dcc6, 0x95a3b3));
        Map<E, Color> colors = new HashMap<>();
        return ImageComponent.imageFrom(imageData, v -> {
            Color c = colors.get(v);
            if (c == null) {
                colors.put(v, c = new Color(palette.isEmpty() ? v.hashCode() : palette.remove(0)));
            }
            return c;
        });
    }

    public static <E> BufferedImage imageFrom(Set<Vec2i> set, Color inSetColor, Color notInSetColor) {
        var extents = Vec2i.getExtents(set);
        Vec2i min = extents.first().sub(new Vec2i(1, 1));
        Vec2i max = extents.second().add(new Vec2i(1, 1));
        return ImageComponent.imageFrom(max.x() - min.x() + 1, max.y() - min.y() + 1,
                (x, y) -> set.contains(new Vec2i(min.x() + x, min.y() + y)) ? inSetColor : notInSetColor);
    }

    public static BufferedImage imageFrom(int width, int height, BiFunction<Integer, Integer, Color> pixels) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                image.setRGB(x, y, pixels.apply(x, y).getRGB());
            }
        }
        return image;
    }

    private BufferedImage image;
    private Vec2d offset = new Vec2d(0.0, 0.0);
    private float scaling = 1.0f;

    public ImageComponent(BufferedImage image) {
        this.setImage(image, true);
    }

    public void setImage(BufferedImage image, boolean resetView) {
        this.image = image;
        if (this.image != null && resetView) {
            this.setPreferredSize(new Dimension(1280, 720));
            new ImageMouseListener();
            float widthScaling = 1280.0f / (float) image.getWidth(this);
            float heightScaling = 720.0f / (float) image.getHeight(this);
            if (1.0f < widthScaling && 1.0f < heightScaling) {
                this.scaling = Util.lowestOf(widthScaling, heightScaling);
            } else if (1.0f < widthScaling) {
                this.scaling = widthScaling;
            } else if (1.0f < heightScaling) {
                this.scaling = heightScaling;
            } else {
                this.scaling = 1.0f;
            }
            this.offset = new Vec2d((1280.0 - (double) this.getImageDimension().x()) / 2.0, 0.0);
        }
        this.repaint();
    }

    public BufferedImage getImage() {
        return this.image;
    }

    @Override
    public void paint(Graphics g) {
        if (this.image != null) {
            Vec2i dim = this.getImageDimension();
            Vec2d p = this.getClampedOffset();
            g.drawImage(this.image, (int) p.x(), (int) p.y(), dim.x(), dim.y(), this);
        }
    }

    private Vec2i getImageDimension() {
        int w = (int) Math.ceil((float) this.image.getWidth(this) * this.scaling);
        int h = (int) Math.ceil((float) this.image.getHeight(this) * this.scaling);
        return new Vec2i(w, h);
    }

    private Vec2d getClampedOffset() {
        Vec2i dim = this.getImageDimension();
        double y = Util.clamp(this.offset.y(), -(double) (dim.y() + 8), (double) (this.getHeight() - 8));
        double x = Util.clamp(this.offset.x(), -(double) (dim.x() + 8), (double) (this.getWidth() - 8));
        return new Vec2d(x, y);
    }

    private void scale(Vec2i fixPoint, float factor) {
        this.offset = Vec2d.of(fixPoint).add(this.offset.sub(Vec2d.of(fixPoint)).mul(factor));
        this.scaling *= factor;
        this.repaint();
    }

    private void pan(Vec2i delta) {
        this.offset = this.offset.add(Vec2d.of(delta));
        this.repaint();
    }
}
