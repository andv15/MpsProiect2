import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class TextOverlay extends JPanel {

    public BufferedImage clearImage;
    public BufferedImage image;
    private String text;
    private double rotateAngle = 0;
    private double posX = 100, posY = 200;
    private double zoom = 1;
    private double speedTranslate = 0.05;
    private Color color = Color.BLACK;
    private Timer timer;
    public int animationIndex = 0;

    public TextOverlay() {
        try {
            image = ImageIO.read(new File("Test.png"));
            clearImage = ImageIO.read(new File("Test.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        image = updateText("");
    }
    
    public void makeHarder() {
    	speedTranslate += 0.02;
    	rotateAngle += 0.001;
    	zoom -= 0.0001;
    }
    
    public void reset() {
    	color = Color.BLACK;
    	rotateAngle = 0;
    	posX = 100; posY = 200;
    	zoom = 1;
    	speedTranslate = 0.05;

    }
    
    public void generateAnimation() {
    	
    	if (animationIndex == 4) {
    		timer.cancel();
    		timer = null;
    		color = Color.BLACK;
    	}
    	
    	if (Window.runda_curenta < 5) {
    		animationIndex = 0;
    	} else {
    		Random rand = new Random();
    		animationIndex = rand.nextInt(100) % 5;
    	}

    	if (animationIndex == 4) {
    		startColorAnimation();
    	}
    }
    
    public void startColorAnimation() {
    	/* la fiecare secunda schimba culoarea text-ului */
		timer = new Timer();
		timer.scheduleAtFixedRate(new ColorChange(), 0, 1000);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(image.getWidth(), image.getHeight());
    }
    
    public void normal(Graphics g) {
        AffineTransform af = AffineTransform.getTranslateInstance(100, 200);
        Graphics2D g2D = (Graphics2D)g;
        g2D.drawImage(image, af, null);    	
    }
    
    public void rotate(Graphics g) {
        rotateAngle += 0.025;
        AffineTransform af = AffineTransform.getTranslateInstance(100, 200);
        af.rotate(Math.toRadians(rotateAngle), image.getWidth()/2, image.getHeight()/2);
        Graphics2D g2D = (Graphics2D)g;
        g2D.drawImage(image, af, null);   	
    }
    
    public void translate(Graphics g) {
    	
        posX = (posX + speedTranslate) % (250 - text.length() - 5);
        AffineTransform af = AffineTransform.getTranslateInstance(posX, posY);
        af.rotate(Math.toRadians(rotateAngle), image.getWidth()/2, image.getHeight()/2);
        Graphics2D g2D = (Graphics2D)g;
        g2D.drawImage(image, af, null);       	
    }
    
    public void scale(Graphics g) {
        AffineTransform af = AffineTransform.getTranslateInstance(posX, posY);
        zoom -= 0.0001;
        af.translate(200 - (image.getWidth() * (zoom))/2, 
        		100 - (image.getHeight() * (zoom))/2);
        af.scale(zoom, zoom);
        Graphics2D g2D = (Graphics2D)g;
        g2D.drawImage(image, af, null);       	
    }
    
    
    public class ColorChange extends TimerTask {

		@Override
		public void run() {
	        if (color == Color.RED) {
	        	color = Color.BLUE;
	        } else {
	        	color = Color.RED;
	        }			
		}
    	
    }

    public BufferedImage updateText(String s) {
        int w = clearImage.getWidth();
        int h = clearImage.getHeight();
        
        text = s;

        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        g2d.drawImage(clearImage, 0, 0, null);
        g2d.setPaint(color);
        g2d.setFont(new Font("Serif", Font.BOLD, 20));
        FontMetrics fm = g2d.getFontMetrics();
        int x = img.getWidth()/2 - fm.stringWidth(s)/2 - 5;
        int y = img.getHeight()/2;
        g2d.drawString(s, x, y);
        g2d.dispose();
        return img;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
       
       	
       	switch(animationIndex) {
        	case 0:
        		normal(g);
        		break;
        	case 1:
        		rotate(g);
        		break;
        	case 2:
        		translate(g);
        		break;
        	case 3:
        		scale(g);
        		break;
        	case 4:
        		image = updateText(text);
        		normal(g);
        		break;
        }
        repaint();
    }

}