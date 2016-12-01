
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public class Window extends JFrame implements KeyListener {

	JTextField name_area_helper = new JTextField("Name : "); // indicator nume
	JTextField name_area = new JTextField(); // name box
	JTextField r_area = new JTextField("Read Box"); // read box
	JTextField w_area = new JTextField(); // write box

	JTextField h_area_helper = new JTextField("Top 10"); // indicator highscore box
	JTextArea h_area = new JTextArea(); // highscores box
	// highscores vector maxim 10 intrari
	ArrayList<Highscore> h_scores = new ArrayList<Highscore>(10);

	JTextField score_area = new JTextField("Score : 0");// score box
	JTextField timer_area = new JTextField("Timer : 0.00");// timer

	String gen_phrase, nume_player; // fraza care trebuie scrisa, numele
									// jucatorului
	Timer time; // timer
	int width, height, scor; // latime, inaltime, scorul jucatorului

	/*
	 * Constructor ce seteaza parametrii ferestrei si parametrii campurilor din
	 * interiorul acesteia
	 */
	public Window(int w, int h) {

		this.setSize(w, h); // setam dimensiunea ferestrei
		this.setVisible(true); // o facem vizibila
		this.setLocationRelativeTo(null); // centram in mijlocul ecranului
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setFocusable(true);
		this.setLayout(null); // pentru a putea folosi set bounds la JTextArea

		this.width = w;
		this.height = h;

		nume_player = "";
		load_highscore(); // initializeaza highscores box

		add(h_area_helper);		// adauga la hereastra top10 box
		add(h_area);	 		// adauga la fereastra highscores box
		add(name_area); 		// adauga la fereastra name box
		add(name_area_helper); 	// adauga la fereastra name_helper box
		add(score_area);		// adauga la fereastra score box
		add(timer_area);		// adauga la fereastra timer box
		add(r_area); 			// adauga la fereastra read box
		add(w_area); 			// adauga la fereastra write box

		// initializare highscores area
		h_area.setEditable(false);
		h_area.setBounds(width / 4, height / 20, width / 2, 170);
		h_area.setToolTipText("Highscores !");
		h_area.setLineWrap(true);
		h_area.setWrapStyleWord(true);
		draw_highscore();

		// initializarea name box
		h_area_helper.setEditable(false);
		h_area_helper.setHorizontalAlignment(JTextField.CENTER);
		h_area_helper.setBounds(width / 8, height / 20 - 3, width / 8, 50);
		h_area_helper.addKeyListener(this);
		
		// initializarea name box
		name_area.setEditable(true);
		name_area.setHorizontalAlignment(JTextField.CENTER);
		name_area.setBounds(2 * width / 8, 4 * height / 10, 5 * width / 8, 50);
		name_area.setToolTipText("Your Name !");
		name_area.addKeyListener(this);

		// initializarea name box
		name_area_helper.setEditable(false);
		name_area_helper.setHorizontalAlignment(JTextField.CENTER);
		name_area_helper.setBounds(width / 8, 4 * height / 10,  width / 8, 50);
		name_area_helper.addKeyListener(this);

		// initializare score box
		score_area.setEditable(false);
		score_area.setBounds(width / 8, 5 * height / 10, 6 * width / 8, 50);
		score_area.setToolTipText("Your score !");
		score_area.setHorizontalAlignment(JTextField.CENTER);

		// initializare timer
		timer_area.setEditable(false);
		timer_area.setHorizontalAlignment(JTextField.CENTER);
		timer_area.setBounds(width / 8, 6 * height / 10, 6 * width / 8, 50);
		timer_area.setToolTipText("Hurry UP !");
		timer_area.setHorizontalAlignment(JTextField.CENTER);

		// initializarea read field
		r_area.setEditable(false);
		r_area.setHorizontalAlignment(JTextField.CENTER);
		r_area.setBounds(width / 8, 7 * height / 10, 6 * width / 8, 50);
		r_area.setToolTipText("Read from here !");
		gen_new_phrase();
		r_area.setText(gen_phrase);

		// initializare write field
		w_area.setEditable(true);
		w_area.setHorizontalAlignment(JTextField.CENTER);
		w_area.setBounds(width / 8, 8 * height / 10, 6 * width / 8, 50);
		w_area.setToolTipText("Your Input here !");
		w_area.addKeyListener(this);

	}

	// Called when the key is pressed down.
	public void keyPressed(KeyEvent e) {

	}

	// Called when the key is released
	public void keyReleased(KeyEvent e) {

		if (e.getKeyCode() == 27) {// check if the Keycode is 27 which is esc
			JOptionPane.showMessageDialog(null, "Good  Bye");// display a good
			// bye messege
			System.exit(0);// exit
		}

		// daca textul din write box este acelasi cu cel din read box
		if (r_area.getText().equalsIgnoreCase(w_area.getText())) {
			scor++; // incrementam scorul
			score_area.setText("Score : " + Integer.toString(scor));
			w_area.setText(""); // resetam scorul din wrtie box
			gen_new_phrase(); // generam o noua propozitie
			draw_read_area();
		}

		set_name();		
		trim_highscore();
		update_highscore();
		save_highscores();
		draw_highscore();

	}

	/*
	 * Deseneaza fraza ce trebuie introdusa de user
	 * in diverse moduri, din ce in ce mai dificil in fucntie de cresterea scorului
	 */
	private void draw_read_area() {
		r_area.setText(gen_phrase);	//TODO sa desenam cu efecte
		
	}

	// Called when a key is typed
	public void keyTyped(KeyEvent e) {

	}

	/*
	 * Seteaza numele jucatorului Numele poate fi editata daca nu ainceput deja
	 * jocul
	 */
	private void set_name() {

		// seteaza numele abia dupa ce am inceput sa scriem in write box
		// dupa ce numele e setat nu s emai poate schimba
		if (name_area.isEnabled() && w_area.getText().length() > 0) {

			nume_player = name_area.getText();

			// daca nu a fost initializat e initializat automat la Player
			if (nume_player.length() == 0) {
				nume_player = "Player";
				name_area.setText(nume_player);
			}

			name_area.setEnabled(false);
		}

	}

	/*
	 * Genereaza o noua propozitie
	 */
	public void gen_new_phrase() {
		gen_phrase = "123";
		// TODO generator de text dintr-o carte?
	}

	///////////////////////////////////////////////////

	/*
	 * Afiseaza scorurile in highscores box
	 */
	private void draw_highscore() {

		String h_str = "";
		int i = 0;
		for (Highscore highscore : h_scores) {
			
			h_str = h_str + ++i +". " + highscore.name + " " + Integer.toString(highscore.score) + "\n";
		}

		h_area.setText(h_str);

	}

	/*
	 * Daca scorul la care a ajuns in sesiunea curent modifica highscores
	 */
	private void update_highscore() {

		// daca nu e nimeni in highscore adauga
		if (h_scores.size() == 0) {
			h_scores.add(new Highscore(nume_player, scor));
			System.out.println("HIGHSCORES EMPTY");
			return;
		}

		// daca jucatorul este deja in highscores doar modifica-i scoruldaca e
		// cazul
		// mentine lista de scoruri ordonata
		for (Highscore highscore : h_scores) {
			if (highscore.name.equalsIgnoreCase(nume_player)) {
				if (highscore.score <= scor) {
					highscore.score = scor;
					// sorteaza descrescator scorurile
					Collections.sort(h_scores, (left, right) -> right.score - left.score);
					// mentine lista de scoruri sub 10 intrari
				}
				return;
			}
		}

		// daca am ajuns aici inseamna ca in tabela exista jucatori si
		// ca jucatorul curent nu se afla deja in tabela
		Highscore h_curent;
		for (int i = 0; i < h_scores.size(); i++) {

			h_curent = h_scores.get(i);
			if (scor > h_curent.score) {
				// de indata ce jucatorul are un highscore mai mare decat cineva
				// din tabela,
				// inseram in fata acestuia
				h_scores.add(i, new Highscore(nume_player, scor));
				return;
			}
		}

	}

	/*
	 * Face o copie a vectorului cu scoruri il goleste si
	 *  pune iar in el in limita a 10 intrari
	 */
	private void trim_highscore() {
		ArrayList<Highscore> h_scores2 = new ArrayList<Highscore>(10);
		for (int i = 0; i < h_scores.size() && i < 10; i++) {
			h_scores2.add(h_scores.get(i));
		}
		
		h_scores.clear();
		h_scores.addAll(h_scores2);
	}

	
	/*
	 * Initializeaza vectorul de scoruri citind dintr-un fisier care are
	 * formatul nume score pe fiecare linie
	 */
	public void load_highscore() {

		// deschide fisier
		FileInputStream fis;
		BufferedReader br = null;

		try {
			fis = new FileInputStream("highscores.txt");
			br = new BufferedReader(new InputStreamReader(fis));
		} catch (FileNotFoundException e) {
			System.out.println("Nu am gasit fisier pentru citire");
		}

		String line = null;
		// parcurge linie cu linie
		try {
			while ((line = br.readLine()) != null) {

				String[] splited = line.split(" ");
				h_scores.add(new Highscore(splited[0], splited[1]));

			}
		} catch (IOException e) {
			System.out.println("Eroare citire");
		}

		// inchide fisier
		try {
			br.close();
		} catch (IOException e) {
			System.out.println("Nu am putut inchide fisierul din care am citit");
			e.printStackTrace();
		}

	}

	/*
	 * Salveaza scorurile in fisier
	 */
	public void save_highscores() {
		// deschide fisier
		OutputStream fis;
		BufferedWriter br = null;

		try {
			fis = new FileOutputStream("highscores.txt");
			br = new BufferedWriter(new OutputStreamWriter(fis));
		} catch (FileNotFoundException e) {
			System.out.println("Eroare deschidere fisier scriere");
			e.printStackTrace();
		}

		String line = "";
		// parcurge linie cu linie
		try {
			for (Highscore highscore : h_scores) {
				line = highscore.name + " " + Integer.toString(highscore.score) + "\n";
				br.write(line);
				line = "";
			}

		} catch (IOException e) {
			System.out.println("Eroare scriere");
			e.printStackTrace();
		}

		// inchide fisier
		try {
			br.close();
		} catch (IOException e) {
			System.out.println("Nu am putut inchide fisierul la scriere");
			e.printStackTrace();
		}

	}

	/////////////////////////////////////////////////

	class Highscore {

		int score;
		String name;

		public Highscore(String string, String string2) {
			this.name = string;
			this.score = Integer.parseInt(string2);
		}

		public Highscore(String nume_player, int scor) {
			this.name = nume_player;
			this.score = scor;
		}

		public int compare(Highscore h2) {
			if (this.score < h2.score) {
				return -1;
			}

			if (this.score == h2.score) {
				return 0;
			}

			return 1;
		}

	}

	public static void main(String[] args) {

		new Window(600, 600);

		// TODO cu timer ca sa se si termine automat

	}

}
