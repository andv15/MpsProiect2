
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagLayout;
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
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.text.JTextComponent;

public class Window extends JFrame implements KeyListener, MouseListener {

	public static final int WRITE_TIME = 10;
	public static final int NR_VIETI = 10;

	JButton play_again = new JButton("Play Again");
	JTextField name_area_helper = new JTextField("Name : "); // indicator nume
	JTextField name_area = new JTextField(); // name box

	JTextField r_area = new JTextField("Read Box"); // read box
	//TODO r_area_image ar trebui sa fie imagine
	//r_area_image.set_text("...");
	
	JTextField w_area = new JTextField(); // write box

	JButton reset_hscore = new JButton("Reset Top");
	JTextField h_area_helper = new JTextField("Top 10"); // indicator highscore
															// box
	JTextArea h_area = new JTextArea(); // highscores box
	// highscores vector maxim 10 intrari
	ArrayList<Highscore> h_scores = new ArrayList<Highscore>(10);

	JTextField score_area = new JTextField();// score box
	JTextField timer_area = new JTextField("Time Left : " + WRITE_TIME + " seconds");// timer

	Timer timer;
	String gen_phrase, nume_player; // fraza care trebuie scrisa, numele
									// jucatorului

	int width, height, scor; // latime, inaltime, scorul jucatorului
	int vieti, runda_curenta; // numar de vieti si runda curenta
	int combo; // se adauga la scor si creste cu +1 la fiecare raspuns corect
	// si scade cu -1 la fiecare raspuns gresit
	
	public long startTime;
	int game_over;	//0 jocul nu s-a terminat,1 s-a terminat

	/////////////////////////////////////////////////

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

		load_highscores(); // initializeaza highscores box

		add(h_area_helper); // adauga la hereastra top10 box
		add(h_area); // adauga la fereastra highscores box
		add(reset_hscore);

		add(play_again);
		add(name_area); // adauga la fereastra name box
		add(name_area_helper); // adauga la fereastra name_helper box

		add(score_area); // adauga la fereastra score box
		add(timer_area); // adauga la fereastra timer box
		add(r_area); // adauga la fereastra read box
		add(w_area); // adauga la fereastra write box

		// initializare highscores area
		h_area.setEditable(false);
		h_area.setBounds(width / 4 + 5, height / 20, width / 2, 170);
		h_area.setToolTipText("Highscores !");
		h_area.setLineWrap(true);
		h_area.setWrapStyleWord(true);
		draw_highscores();

		// initializarea name box
		h_area_helper.setEditable(false);
		h_area_helper.setHorizontalAlignment(JTextField.CENTER);
		h_area_helper.setBounds(width / 8, height / 20 - 4, width / 8, 50);
		h_area_helper.addKeyListener(this);

		// initializarea name box
		reset_hscore.setBounds(width / 8, height / 10, width / 8, 50);
		reset_hscore.addMouseListener(this);

		// initializarea name box
		play_again.setBounds(width / 8, 2 * height / 10, width / 8, 50);
		play_again.addMouseListener(this);

		// initializarea name box
		name_area.setEditable(true);
		name_area.setHorizontalAlignment(JTextField.CENTER);
		name_area.setBounds(2 * width / 8, 4 * height / 10, 5 * width / 8, 50);
		name_area.setToolTipText("Your Name !");
		name_area.addKeyListener(this);

		// initializarea name box
		name_area_helper.setEditable(false);
		name_area_helper.setHorizontalAlignment(JTextField.CENTER);
		name_area_helper.setBounds(width / 8, 4 * height / 10, width / 8, 50);
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

		init_game();

	}

	/*
	 * metoda folosita pentru initializarea jocului si reinitializarea lui
	 */
	private void init_game() {

		// Daca gasim un mod de a reseta timpul si de al controla
		// sa nu porneasca singur cand apasam pe PlayAgain
		// punem si campurile astea

		// nume_player = "Player";
		// name_area.setEnabled(true);
		// name_area.setText("");
		w_area.setEnabled(true);
		w_area.setText("");
		w_area.setBackground(Color.white);

		timer_area.setBackground(Color.WHITE);
		timer_area.setText("Time Left : " + WRITE_TIME + " seconds");

		game_over = 0;
		scor = 0; // latime, inaltime, scorul jucatorului
		vieti = NR_VIETI;
		runda_curenta = 0;
		combo = 0;
		startTime = -1;

		score_area.setText("	Vieti " + vieti + "	Score : " + Integer.toString(scor) + "	Combo : " + combo
				+ "	Runda : " + runda_curenta);

		// ceva asemanator gasit pe stackoverflow
		timer = new Timer(60, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (startTime == -1) {
					startTime = System.nanoTime();
				} else {
					long endTime = startTime + TimeUnit.SECONDS.toNanos(WRITE_TIME);
					long time = System.nanoTime();

					if (time < endTime) {
						long timeLeft = (endTime - time);
						long seconds = timeLeft / 1000000000;
						long dec = (timeLeft / 10000000) % 100;

						// daca ne apropiem de tarminarea timpului il face rosu
						if (seconds < 5) {
							timer_area.setForeground(Color.red);
						} else {
							timer_area.setForeground(Color.black);
						}

						timer_area.setText(String.format("Time Left : %d.%02d seconds", seconds, dec));
					} else {

						timer_area.setText("Time's Up");

						((Timer) e.getSource()).stop(); // opreste listnerul

						// cand timpul expira jocul se termina
						end_game();
					}
					revalidate();
					repaint();
				}
			}

		});

		timer.setInitialDelay(0);

	}

	/*
	 * Seteaza campurile din fereastra conform starii de terminare a jocului
	 * Salveaza tabelul de scoruri
	 */
	protected void end_game() {

		// ca sa nu executam de mai mutle ori functia asta
		if (game_over == 1) {
			return;
		}

		// cand expira timpul blocheaza campul de write
		w_area.setText("GAME OVER");
		w_area.setEnabled(false);
		reset_hscore.setEnabled(true);
		score_area.setText("	Vieti " + vieti + "	Score : " + Integer.toString(scor) + "	Combo : " + combo
				+ "	Runda : " + runda_curenta);

		// actualiza tabela de highscores
		update_highscores();
		trim_highscores();
		save_highscores();
		draw_highscores();

		game_over = 1;
	}

	/////////////////////////////////////////////////

	// Called when the key is pressed down.
	public void keyPressed(KeyEvent e) {

		if (e.getKeyCode() == 27) {// check if the Keycode is 27 which is esc
			JOptionPane.showMessageDialog(null, "Good  Bye");// display a good
			// bye messege
			System.exit(0);// exit
		}

		// daca a apasat enter SI daca era cursorul in casuta de scriere
		if (e.getKeyCode() == KeyEvent.VK_ENTER && w_area.isFocusOwner()) {

			// daca textul din write box este acelasi cu cel din read box
			if (r_area.getText().equalsIgnoreCase(w_area.getText())) {

				w_area.setBackground(Color.green); // desenaza-l verde
				scor = scor + combo + runda_curenta; // incrementam scorul
				runda_curenta++;
				combo++;
				vieti++;

				startTime = -1; // resetam timpul, pentru urmatoarea runda

				gen_new_phrase(); // generam o noua propozitie
				draw_read_area();
				

			} else { // daca am scris gresit
				w_area.setBackground(Color.red); // w_area se face rosie
				combo--; // combo scade
				vieti--; // scad vieti

				// daca ramanem fara vieti jocul se termina
				if (vieti == 0) {
					end_game();
					return;
				}

				w_area.setText("");
			}

			score_area.setText("	Vieti " + vieti + "	Score : " + Integer.toString(scor) + "	Combo : " + combo
					+ "	Runda : " + runda_curenta);

			// dupa primul ENTER porneste timpul si seteaza numele
			timer.start();
			set_name();

			// in timpul unei sesiuni de joc nu se poate modifica tabela de
			// scoruri
			reset_hscore.setEnabled(false);
		}

	}

	// Called when a key is typed
	public void keyTyped(KeyEvent e) {

	}

	// Called when the key is released
	public void keyReleased(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_ENTER) {

			w_area.setBackground(Color.WHITE);
		}

	}

	/////////////////////////////////////////////////

	@Override
	public void mouseClicked(MouseEvent e) {

		// daca reset e in focus(daca e moseul pe el)
		if (reset_hscore.isFocusOwner()) {
			reset_highscores();
			draw_highscores();
		}

		// daca e apasat play again
		if (play_again.isFocusOwner()) {
			init_game();
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	///////////////////////////////////////////////////

	/*
	 * Deseneaza fraza ce trebuie introdusa de user in diverse moduri, din ce in
	 * ce mai dificil in fucntie de cresterea scorului
	 */
	private void draw_read_area() {
		r_area.setText(gen_phrase); // TODO sa desenam cu efecte
		
	}

	/*
	 * Seteaza numele jucatorului Numele poate fi editata daca nu ainceput deja
	 * jocul
	 */
	private void set_name() {

		// seteaza numele abia dupa ce am inceput sa scriem in write box
		// dupa ce numele e setat nu s emai poate schimba
		if (name_area.isEnabled()) {

			nume_player = name_area.getText();

			// daca nu a fost initializat e initializat automat la Player
			if (nume_player.length() == 0) {
				nume_player = "Player";
				name_area.setText(nume_player);
			}

			// cand incepe scrierea in campul write
			// se blocheaza schimbarea de nume si porneste timerul
			name_area.setEnabled(false);

		}

	}

	/*
	 * Genereaza o noua propozitie 
	 */
	public void gen_new_phrase() {

		//TODO
		// initializare
		//gen_phrase = "Fraza lunga sa vedem cat de mult loc avem.";

		gen_phrase = "";
		// deschide fisierul miraculos
		Scanner input = null;
		try {
			input = new Scanner(new FileInputStream("miracole.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("Nu am putut deschide fisier miraculos");
			e.printStackTrace();
		}

		// sari peste ce am scris deja
		for (int i = 0; i < 2 * scor; i++) {
			input.next();
		}

		for (int i = 0; i < 2 * scor + 1; i++) {
			if (input != null) {

				// daca e ultima iteratie nu mai pune spatiul
				if (i == 2 * scor) {
					gen_phrase += input.next();
				} else {
					gen_phrase += input.next() + " ";
				}
			} else {
				System.out.println("Nu am putut citi din fisierul cu miracole");
			}
		}

		// inchide fisierul miraculos
		// inchide fisier

		input.close();

	}

	///////////////////////////////////////////////////

	/*
	 * Afiseaza scorurile in highscores box
	 */
	private void draw_highscores() {

		String h_str = "";
		int i = 0;
		for (Highscore highscore : h_scores) {

			h_str = h_str + ++i + ". " + highscore.name + " " + Integer.toString(highscore.score) + "\n";
		}

		h_area.setText(h_str);

	}

	/*
	 * Daca scorul la care a ajuns in sesiunea curent modifica highscores
	 */
	private void update_highscores() {

		// daca e loc in tabela de highscore il punem
		if (h_scores.size() < 10) {
			h_scores.add(new Highscore(nume_player, scor));
			Collections.sort(h_scores, (left, right) -> right.score - left.score);
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
				Collections.sort(h_scores, (left, right) -> right.score - left.score);
				return;
			}
		}

	}

	/*
	 * Face o copie a vectorului cu scoruri il goleste si pune iar in el in
	 * limita a 10 intrari
	 */
	private void trim_highscores() {
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
	public void load_highscores() {

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

	public void reset_highscores() {

		h_scores.clear();
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

		new Window(800, 800);

	}

}
