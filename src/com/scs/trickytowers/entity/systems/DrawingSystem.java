package com.scs.trickytowers.entity.systems;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

import com.scs.trickytowers.BodyUserData;
import com.scs.trickytowers.MainWindow;
import com.scs.trickytowers.Main_TumblyTowers;
import com.scs.trickytowers.Statics;

public class DrawingSystem {
	
	private Main_TumblyTowers game;
	public Vec2 cam_centre_logical = new Vec2();
	private Stroke stroke;

	public DrawingSystem(Main_TumblyTowers _game) {
		game = _game;
		stroke = new BasicStroke(1, 0, 0 , 1000);
	}


	public void startOfDrawing(Graphics g) {
		// Default to centre
		cam_centre_logical.x = Statics.WORLD_WIDTH_LOGICAL/2;
		cam_centre_logical.y = Statics.WORLD_HEIGHT_LOGICAL/2;
	}


	public void endOfDrawing() {
	}


	public void getPixelPos(Point ret, Vec2 worldpos) {
		int x1 = (int)((worldpos.x-cam_centre_logical.x) * Statics.LOGICAL_TO_PIXELS + (game.window.getWidth()/2));
		int y1 = (int)((worldpos.y-cam_centre_logical.y) * Statics.LOGICAL_TO_PIXELS + (game.window.getHeight()/2));
		ret.x = x1;
		ret.y = y1;
	}


	public void drawImage(Point tmp, BufferedImage img, Graphics g, Body b) {
		Vec2 worldpos = b.getPosition();
		getPixelPos(tmp, worldpos);
		int rad = img.getWidth()/2;
		g.drawImage(img, (int)(tmp.x-rad), (int)(tmp.y-rad), null);

	}


	public void drawDot(Point tmp, Graphics g, Vec2 worldpos, Color c) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setStroke(stroke);
		g2.setColor(c);

		getPixelPos(tmp, worldpos);
		g.drawRect(tmp.x, tmp.y, 1, 1);

	}


	public void drawShape(Point tmp, Graphics g, Body b) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setStroke(stroke);

		if (b == null) {
			return;
		}
		
		Fixture f = b.getFixtureList();
		if (f == null) {
			BodyUserData bud = (BodyUserData)b.getUserData();
			Statics.p("WARNING: " + bud.name + " has no fixture");
		}
		while (f != null) {
			Color col = Color.gray;
			BodyUserData userdata = (BodyUserData)f.getUserData();
			if (userdata != null && userdata.col != null) {
				col = userdata.col;
			}
			Color darker = col.darker().darker();

			if (f.getShape() instanceof PolygonShape) {
				Polygon polygon = new Polygon();
				PolygonShape shape = (PolygonShape)f.getShape();
				for (int i=0 ; i<shape.getVertexCount() ; i++) {
					Vec2 v = shape.getVertex(i);
					getPixelPos(tmp, b.getWorldPoint(v));
					polygon.addPoint(tmp.x, tmp.y);
				}
				g.setColor(darker);
				g.fillPolygon(polygon);
				g.setColor(col);
				g.drawPolygon(polygon);

			} else if (f.getShape() instanceof EdgeShape) {
				EdgeShape shape = (EdgeShape)f.getShape();
				Vec2 prev = shape.m_vertex1;
				Vec2 v = shape.m_vertex2;
				Vec2 worldpos = b.getWorldPoint(prev);
				Point p = new Point();
				getPixelPos(p, worldpos);
				worldpos = b.getWorldPoint(v);
				getPixelPos(tmp, worldpos);

				g.setColor(col);
				g.drawLine(p.x, p.y, tmp.x, tmp.y);

			} else if (f.getShape() instanceof CircleShape) {
				CircleShape shape2 = (CircleShape)f.getShape();
				Vec2 worldpos = b.getPosition();
				getPixelPos(tmp, worldpos);
				int rad = (int)(shape2.getRadius() * Statics.LOGICAL_TO_PIXELS);
				if (rad < 1) {
					rad = 1;
				}
				g.setColor(darker);
				g.fillOval((int)(tmp.x-rad), (int)(tmp.y-rad), rad*2, rad*2);
				g.setColor(col);
				g.drawOval((int)(tmp.x-rad), (int)(tmp.y-rad), rad*2, rad*2);

			} else {
				throw new RuntimeException("Cannot draw " + b);
			}

			f = f.getNext();

		}
	}

	public void paintFixedMenu(java.awt.Graphics g, MainWindow m, float volume) {
		Font fontMenu = new Font("Showcard Gothic", Font.PLAIN, 12);
		g.setFont(fontMenu);
		g.setColor(Color.black);
	
		// Definindo o texto do menu de comandos
		String[] menuLines = {
			"[R] - Restart  [K - L] - Change Background  |  [W, A, S, D, SPACE] - Player 1  |  [Arrow Keys, Ctrl] - Player 2",
			"[ESQ] - EXIT"
		};
	
		int lineSpacing = 5;
	
		// Tamanho da Tela
		int screenWidth = m.getWidth();
	
		// Desenha o menu de comandos no topo da tela
		FontMetrics metrics = g.getFontMetrics(fontMenu);
		int y = metrics.getHeight() + 50; // Alinhado no topo com margem de 10 pixels
	
		for (String line : menuLines) {
			// Calcula a largura do texto para centralizar
			int textWidth = metrics.stringWidth(line);
			int x = (screenWidth - textWidth) / 2; // Centraliza horizontalmente
	
			// Desenha a linha do menu de comandos
			g.drawString(line, x, y);
			y += metrics.getHeight() + lineSpacing; // Move para a próxima linha
		}
	
		// Desenhar a barra de volume
		drawVolumeBar(g, m, volume); // Chama a função para desenhar a barra de volume
	}
	
	// Função para desenhar a barra de volume
	public void drawVolumeBar(Graphics g, MainWindow m, float volume) {
		int maxVolume = 5; // Volume máximo, ou seja, 5 barras
		int barWidth = 20; // Largura de cada barra
		int barHeight = 10; // Altura de cada barra
		int spacing = 5; // Espaçamento entre as barras
	
		// Define a posição da barra de volume à esquerda da tela
		int x = 40; // Justificado à esquerda, com margem de 40 pixels
		int y = m.getHeight() - barHeight - 40; // Posição no rodapé, com margem de 40 pixels da borda inferior
	
		// Desenha as barras de volume
		for (int i = 0; i < maxVolume; i++) {
			if (i < volume) {
				g.setColor(Color.GREEN); // Barra preenchida
			} else {
				g.setColor(Color.GRAY); // Barra vazia
			}
			g.fillRect(x, y, barWidth, barHeight); // Desenha a barra
			x += barWidth + spacing; // Move para a próxima barra
		}
	
		// Instruções para ajustar o volume
		g.setColor(Color.WHITE);
		String instructions = "Press '+' to increase, '-' to decrease";
		g.drawString(instructions, 40, y + barHeight + 30); // Exibe as instruções logo abaixo das barras
	
		// Desenha os sinais de + e - nas laterais da barra de volume
		String plus = "+";
		String minus = "-";
		
		// Coloca o sinal de + do lado direito das barras
		g.drawString(plus, x, y + barHeight / 2 + 5); // Exibe o sinal de + ao lado direito das barras
		
		// Coloca o sinal de - do lado esquerdo das barras (40 é a margem inicial da barra)
		g.drawString(minus, 20, y + barHeight / 2 + 5); // Exibe o sinal de - ao lado esquerdo das barras
	
		// Exibe a porcentagem de volume à esquerda, abaixo das barras
		String volumeText = "Volume: " + (int)((volume / maxVolume) * 100) + "%";
		g.drawString(volumeText, 40, y + barHeight + 15); // Texto de volume
	}
	

}	
