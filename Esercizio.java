//LEGGERE LE ISTRUZIONI NEL FILE README.md

//Import di Classi Java necessarie al funzionamento del programma
import java.util.Scanner;
import java.io.*;
// Classe principale, con metodo main
class Esercizio {
    // Il programma parte con una chiamata a main().
    public static Scanner input = new Scanner(System.in);

	public static double calcolaPesoIdeale(double altezzaCm, int eta, String genere) {
		String inizialeGenere = genere.substring(0,1);
		double[] metodiPeso = new double[9];

		if(inizialeGenere.equalsIgnoreCase("m")) {
			metodiPeso[0] = altezzaCm - 100 - (altezzaCm - 150)/4; 
			metodiPeso[1] = altezzaCm - 100;
			metodiPeso[2] = (altezzaCm - 150) * 0.75 + 50;
			metodiPeso[5] = Math.pow((altezzaCm/100) , 2) * 22.1;
			metodiPeso[8] = (1.40 * Math.pow(altezzaCm / 10.0, 3)) / 100.0; 
		}else {
			metodiPeso[0] = altezzaCm - 100 - (altezzaCm - 150)/2;
			metodiPeso[1] = altezzaCm - 104;
			metodiPeso[2] = (altezzaCm - 150) * 0.6 + 50;
			metodiPeso[5] = Math.pow((altezzaCm/100) , 2) * 20.6;
			metodiPeso[8] = (135 * Math.pow(altezzaCm / 10.0, 3)) / 100.0;
		}

		metodiPeso[3] = 0.8 * (altezzaCm - 100) + eta / 2.0;
		metodiPeso[4] = altezzaCm - 100 + eta / 10.0 * 0.9;
		metodiPeso[6] = (1.012 * altezzaCm) - 107.5;
		metodiPeso[7] = Math.pow((2.37 * (altezzaCm / 100)), 3);

		for(int i = 0; i < metodiPeso.length; i++) {
			metodiPeso[i] = Math.floor(metodiPeso[i] * 10) / 10;
		}

		System.out.println("\nSeleziona un metodo per il calcolo del peso ideale:");
		System.out.println("1. Lorenz\n2. Broca\n3. Wan der Vael\n4. Berthean\n5. Perrault\n6. Keys\n7. Travia\n8. Livi\n9. Buffon, Roher e Bardeen\n");

		int scelta;
		do {
			System.out.print("Scelta (1-9): ");
			scelta = Integer.parseInt(input.nextLine());
			if(scelta < 1 || scelta > 9) {
				System.out.println("Valore non valido, riprova.");
			}
		} while(scelta < 1 || scelta > 9);

		return metodiPeso[scelta - 1];
	}

	public static double calcolaFabbisognoCalorico(double peso, int eta, String genere) {
		double fabbisogno;
		String inizialeGenere = genere.substring(0,1);

		if(inizialeGenere.equalsIgnoreCase("m")) {
			fabbisogno = eta <= 29 ? 15.3 * peso + 679 :
						eta <= 59 ? 11.6 * peso + 879 :
						eta <= 74 ? 11.9 * peso + 700 : 8.4 * peso + 819;
		} else {
			fabbisogno = eta <= 29 ? 14.7 * peso + 496 :
						eta <= 59 ? 8.7 * peso + 829 :
						eta <= 74 ? 9.2 * peso + 688 : 9.8 * peso + 624;
		}

		fabbisogno = Math.floor(fabbisogno * 10) / 10;
		System.out.println("\nFabbisogno calorico giornaliero: " + fabbisogno + " kcal\n");
		return fabbisogno;
	}

	public static void registraConsumoCalorico(double fabbisogno) {
		boolean alimentoTrovato = false;
		String linea, nomeCibo;
		int quantita;
		double kcalSingole = 0;
		double totaleCalorie = 0;
		String continuaInserimento = "s";

		try {
			FileWriter fileUscita = new FileWriter("Giornaliero.csv", true);
			Scanner scannerFileGiornaliero = new Scanner(new File("Giornaliero.csv"));

			while(scannerFileGiornaliero.hasNextLine()) {
				totaleCalorie += Double.parseDouble(scannerFileGiornaliero.nextLine().split("\\|")[2]);
			}
			scannerFileGiornaliero.close();

			while(continuaInserimento.equalsIgnoreCase("s") && totaleCalorie < fabbisogno) {
				System.out.print("Nome cibo consumato: ");
				nomeCibo = input.nextLine().toLowerCase().replaceAll("[^a-z]", "");

				Scanner scannerAlimenti = new Scanner(new File("Calorie.csv"));
				scannerAlimenti.nextLine();

				do {
					System.out.print("Quantità consumata: ");
					quantita = Integer.parseInt(input.nextLine());
					if(quantita < 0) System.out.println("Quantità non valida.");
				} while(quantita < 0);

				while(scannerAlimenti.hasNextLine() && !alimentoTrovato) {
					linea = scannerAlimenti.nextLine();
					String[] campi = linea.split("\\|");
					if(campi[0].toLowerCase().replaceAll("[^a-z]", "").equals(nomeCibo)) {
						kcalSingole = Double.parseDouble(campi[2]);
						alimentoTrovato = true;
					}
				}
				scannerAlimenti.close();

				if(!alimentoTrovato) {
					System.out.println("Alimento non trovato nel database.");
				} else {
					double calorieAggiunte = kcalSingole * quantita;
					totaleCalorie += calorieAggiunte;
					fileUscita.write(nomeCibo + "|" + quantita + "|" + calorieAggiunte + "\n");
				}

				if(totaleCalorie < fabbisogno) {
					System.out.print("Vuoi aggiungere un altro alimento? (s/n): ");
					continuaInserimento = input.nextLine().substring(0,1);
				}
				alimentoTrovato = false;
			}

			System.out.println("\nTotale calorie consumate: " + totaleCalorie + " kcal");
			if(totaleCalorie > fabbisogno) {
				System.out.println("Hai superato il tuo fabbisogno di " + (totaleCalorie - fabbisogno) + " kcal.");
			} else {
				System.out.println("Ti restano " + (fabbisogno - totaleCalorie) + " kcal da consumare oggi.");
			}

			fileUscita.close();

			Scanner letturaFinale = new Scanner(new File("Giornaliero.csv"));
			System.out.println("\nRiepilogo alimenti consumati oggi:");
			while(letturaFinale.hasNextLine()) {
				System.out.println(letturaFinale.nextLine());
			}
			letturaFinale.close();
		
		} catch(IOException e) {
			System.out.println("Errore durante l'accesso ai file: " + e.getMessage());
		}
	}

	// Il resto del codice principale verrà aggiornato in seguito

    public static void main(String[] args) {
    
    int opzione;
    double altezzaUtente;
    int etaUtente;
    String genereUtente;
    double pesoIdeale = 0;
    double fabbisognoCalorico = 0;
    String inizialeGenere;

    try {
        File fileInfoUtente = new File("info.txt");

        if (!fileInfoUtente.exists()) {

            fileInfoUtente.createNewFile();

            do {
                System.out.print("Inserire la propria altezza (in cm): ");
                altezzaUtente = Double.parseDouble(in.nextLine());
                if (altezzaUtente < 0 || altezzaUtente > 270) {
                    System.out.println("Inserire un valore valido!");
                }
            } while (altezzaUtente < 0 || altezzaUtente > 270);

            do {
                System.out.print("Inserire la propria età: ");
                etaUtente = Integer.parseInt(in.nextLine());
                if (etaUtente < 0) {
                    System.out.println("Inserire un valore valido!");
                }
            } while (etaUtente < 0);

            do {
                System.out.print("Inserire sesso (maschio / donna): ");
                genereUtente = in.nextLine();
                inizialeGenere = genereUtente.substring(0, 1);
                if (!inizialeGenere.equalsIgnoreCase("m") && !inizialeGenere.equalsIgnoreCase("d")) {
                    System.out.println("Inserire un valore valido!");
                }
                if (inizialeGenere.equalsIgnoreCase("m")) {
                    genereUtente = "maschio";
                }
                if (inizialeGenere.equalsIgnoreCase("d")) {
                    genereUtente = "donna";
                }
            } while (!inizialeGenere.equalsIgnoreCase("m") && !inizialeGenere.equalsIgnoreCase("d"));

        } else {
            Scanner lettore = new Scanner(new File("info.txt"));
            String riga;
            String[] datiUtente = new String[5];

            for (int i = 0; i < datiUtente.length; i++) {
                riga = lettore.nextLine();
                String[] valori = riga.split(":");
                datiUtente[i] = valori[valori.length - 1];
            }

            altezzaUtente = Double.parseDouble(datiUtente[0]);
            etaUtente = Integer.parseInt(datiUtente[1]);
            genereUtente = datiUtente[2];
            pesoIdeale = Double.parseDouble(datiUtente[3]);
            fabbisognoCalorico = Double.parseDouble(datiUtente[4]);

            lettore.close();
        }

        do {

            System.out.println();
            System.out.println("INFORMAZIONI:");
            System.out.println("Altezza: " + altezzaUtente + "\nEtà: " + etaUtente + "\nSesso: " + genereUtente + "\nPeso: " + pesoIdeale + "\nFabisongo: " + fabbisognoCalorico);
            System.out.println();

            System.out.print("Scegliere operazione da eseguire: \n1. Calcolo peso corporeo \n2. Fabbisogno calorico giornaliero \n3. Controllo giornaliero delle calorie \n4. Reset giornaliero \n5. Cambia informazioni \n6. Esci \nScelta: ");
            opzione = Integer.parseInt(in.nextLine());

            if (opzione == 1) {
                pesoIdeale = calcoloPesoCorporeo(altezzaUtente, etaUtente, genereUtente);
            }
            if (opzione == 2 && pesoIdeale != 0) {
                fabbisognoCalorico = calcoloFabbisognoCaloricoGiornaliero(pesoIdeale, etaUtente, genereUtente);
            } else {
                if (opzione == 2 && pesoIdeale == 0) {
                    System.out.println();
                    System.out.println("Calcolo del peso non effettuato impossibile eseguire l'operazione!");
                }
            }

            if (opzione == 3 && fabbisognoCalorico != 0) {
                controlloGiornalieroCalorie(fabbisognoCalorico);
            } else {
                if (opzione == 3 && fabbisognoCalorico == 0) {
                    System.out.println();
                    System.out.println("Calcolo del fabbisogno calorico giornaliero non effettuato!");
                }
            }

            if (opzione == 4) {
                try {
                    FileWriter pulisci = new FileWriter("Giornaliero.csv", false);
                    pulisci.write("");
                    pulisci.close();
                } catch (IOException e) {
                    System.out.println("C'e stato un errore! Errore: " + e.getMessage());
                }
                System.out.println("File cancellato con successo!");
            }

            if (opzione == 5) {
                do {
                    System.out.print("Inserire la propria altezza (in cm): ");
                    altezzaUtente = Double.parseDouble(in.nextLine());
                    if (altezzaUtente < 0 || altezzaUtente > 270) {
                        System.out.println("Inserire un valore valido!");
                    }
                } while (altezzaUtente < 0 || altezzaUtente > 270);

                do {
                    System.out.print("Inserire la propria età: ");
                    etaUtente = Integer.parseInt(in.nextLine());
                    if (etaUtente < 0) {
                        System.out.println("Inserire un valore valido!");
                    }
                } while (etaUtente < 0);

                do {
                    System.out.print("Inserire sesso (maschio / donna): ");
                    genereUtente = in.nextLine();
                    inizialeGenere = genereUtente.substring(0, 1);
                    if (!inizialeGenere.equalsIgnoreCase("m") && !inizialeGenere.equalsIgnoreCase("d")) {
                        System.out.println("Inserire un valore valido!");
                    }
                    if (inizialeGenere.equalsIgnoreCase("m")) {
                        genereUtente = "maschio";
                    }
                    if (inizialeGenere.equalsIgnoreCase("d")) {
                        genereUtente = "donna";
                    }

                } while (!inizialeGenere.equalsIgnoreCase("m") && !inizialeGenere.equalsIgnoreCase("d"));

                fabbisognoCalorico = 0;
                pesoIdeale = 0;

                System.out.println();
                System.out.println("Informazioni aggiornate con successo!");
                System.out.println();
            }

            if (opzione == 6) {
                FileWriter salva = new FileWriter("info.txt");

                salva.write("Altezza:" + altezzaUtente);
                salva.write(System.lineSeparator());

                salva.write("Età:" + etaUtente);
                salva.write(System.lineSeparator());

                salva.write("Sesso:" + genereUtente);
                salva.write(System.lineSeparator());

                salva.write("Peso:" + pesoIdeale);
                salva.write(System.lineSeparator());

                salva.write("Fabbisogno:" + fabbisognoCalorico);
                salva.write(System.lineSeparator());

                salva.close();
            }

            if (opzione < 1 || opzione > 6) {
                System.out.println("Inserire un valore valido!");
            }

        } while (opzione != 6);

    } catch (IOException e) {
        System.out.println("C'e stato un errore! Errore: " + e.getMessage());
    }

}


//LEGGERE LE ISTRUZIONI NEL FILE README.md