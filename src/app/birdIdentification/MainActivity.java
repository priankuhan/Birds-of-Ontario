//Prian Kuhanandan
//Ontario Birds Android App: Game to help people learn the names of common birds in Ontario. 
//Shows Pictures of birds and gets user to guess the name of it.

package app.birdIdentification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	//Declares objects and variables
	protected EditText fBirdName;
	protected Spinner fBirdNameSpin;
	protected ImageView fBirdPic;
	protected TextView fProgress;
	protected CheckBox fBeginner;

	protected int fGeneral;
	protected int fCity;
	protected int fWetland;
	protected int fBoP;
	protected int fWoodpeckers;

	protected Chronometer fTimer;
	protected Chronometer fTimerEasy;

	protected TextView fBest;

	Activity context;

	MediaPlayer mp;

	int i = 0;
	boolean easy = false;

	ArrayList<Integer> pics;
	ArrayList<Integer> calls;
	ArrayList<String> names;
	ArrayList<String> altNames;

	long[] times = new long[5];
	long[] easyTimes = new long[5];

	boolean goMenu = false;
	
	//Stops the media player from playing the current soundclip
	private void stopPlaying() {
		//If mediaplayer is playing a song...
        if (mp != null) {
        	//Stops, releases and sets it to null
            mp.stop();
            mp.release();
            mp = null;
       }
    }

	//Recreates app 
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void reload() {
		
		//If the android version is recent enough, uses the recreate method. If not, does it manually
		if (Build.VERSION.SDK_INT >= 11) {
			recreate();
		} else {
			Intent intent = getIntent();
			overridePendingTransition(0, 0);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			finish();

			overridePendingTransition(0, 0);
			startActivity(intent);
		}
	}

	@Override
	//If user presses back button, reloads in order to go back to menu or closes app, depending on when the function is called.
	public void onBackPressed() {
		//Stops sound clip when user tries to leave game 
		stopPlaying();
		if (goMenu) {
			reload();
		} else {
			context.finish();
			System.exit(0);
		}
	}
	
	//Reads in best time records
	public void read() {

		Scanner s = null;

		try {
			//Opens file 
			FileInputStream fIn = openFileInput("birdTimes.txt");
			InputStreamReader isr = new InputStreamReader(fIn);
			
			//Splits input using a delimiter of ">"
			s = new Scanner(new BufferedReader(isr)).useDelimiter("\\s*>\\s*");
			
			//Reads in best times for corresponding level for normal game
			for (int i = 0; i < times.length; i++) {
				long time = s.nextLong();
				if (time != 0){
					times[i] = time;
				} else{
					//If best time is at the default of 0, sets best time to max value of long
					times[i] = Long.MAX_VALUE;
				}
			}
			//Reads in best times for corresponding level for beginner game
			for (int i = 0; i < times.length; i++) {
				long time = s.nextLong();
				if (time != 0){
					easyTimes[i] = time;
				} else{
					//If best time is at the default of 0, sets best time to max value of long
					easyTimes[i] = Long.MAX_VALUE;
				}
			}
			//Closes scanner
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//Closes scanner
			s.close();
		}

	}

	//Writes best time records to file
	public void write() {
		
		FileOutputStream outputStream = null;
		
		//Creates string of best scores for each set in right order
		String string = times[0] + ">" + times[1] + ">" + times[2] + ">"
				+ times[3] + ">" + times[4] + ">" + easyTimes[0] + ">"
				+ easyTimes[1] + ">" + easyTimes[2] + ">" + easyTimes[3] + ">"
				+ easyTimes[4];

		try {
			//Opens birdTimes file in MODE_PRIVATE so it can't be edited by other apps, etc
			outputStream = openFileOutput("birdTimes.txt", Context.MODE_PRIVATE);
			//Writes the string of times to file
			outputStream.write(string.getBytes());
			//Closes file
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		//Sets Content View to menu screen
		setContentView(R.layout.menu);
		//Reads in best time records
		read();
		
		//Initializes buttons
		fGeneral = R.id.button2;
		fCity = R.id.button1;
		fWetland = R.id.button3;
		fBoP = R.id.button4;
		fWoodpeckers = R.id.button5;
		
		//Initializes beginner mode checkbox
		fBeginner = (CheckBox) findViewById(R.id.checkBox1);
		
		context = this;
		
		//If user chooses general, sets picture, sounds, names and alternate names lists accordingly
		findViewById(fGeneral).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				pics = new ArrayList<Integer>(Arrays.asList(
						R.drawable.scarlet_tanager, R.drawable.cardinal,
						R.drawable.blue_jay, R.drawable.robin,
						R.drawable.bc_chickadee, R.drawable.mourning_dove,
						R.drawable.european_starling, R.drawable.pigeon,
						R.drawable.house_wren, R.drawable.song_sparrow,
						R.drawable.cedar_waxwing, R.drawable.goose,
						R.drawable.great_blue_heron, R.drawable.tundra_swan,
						R.drawable.ring_billed_gull, R.drawable.mallard,
						R.drawable.loon, R.drawable.sandhill_crane,
						R.drawable.mute_swan, R.drawable.red_winged,
						R.drawable.belted_kingfisher, R.drawable.osprey,
						R.drawable.bald_eagle, R.drawable.barred_owl,
						R.drawable.sharp_shinned, R.drawable.peregrine_falcon,
						R.drawable.northern_harrier, R.drawable.snowy_owl,
						R.drawable.great_horned_owl,
						R.drawable.red_tailed_hawk, R.drawable.coopers_hawk,
						R.drawable.yellow_bellied, R.drawable.downy,
						R.drawable.pileated, R.drawable.red_headed,
						R.drawable.black_backed, R.drawable.red_bellied,
						R.drawable.hairy, R.drawable.flicker,
						R.drawable.purple_finch));
				calls = new ArrayList<Integer>(Arrays.asList(
						R.raw.scarlet_tanager, R.raw.cardinal, R.raw.blue_jay,
						R.raw.robin, R.raw.bc_chickadee, R.raw.mourning_dove,
						R.raw.european_starling, R.raw.pigeon,
						R.raw.house_wren, R.raw.song_sparrow,
						R.raw.cedar_waxwing, R.raw.goose,
						R.raw.great_blue_heron, R.raw.tundra_swan,
						R.raw.ring_billed_gull, R.raw.mallard, R.raw.loon,
						R.raw.sandhill_crane, R.raw.mute_swan,
						R.raw.red_winged, R.raw.belted_kingfisher,
						R.raw.osprey, R.raw.bald_eagle, R.raw.barred_owl,
						R.raw.sharp_shinned, R.raw.peregrine_falcon,
						R.raw.northern_harrier, R.raw.snowy_owl,
						R.raw.great_horned_owl, R.raw.red_tailed_hawk,
						R.raw.coopers_hawk, R.raw.yellow_bellied, R.raw.downy,
						R.raw.pileated, R.raw.red_headed, R.raw.black_backed,
						R.raw.red_bellied, R.raw.hairy, R.raw.flicker,
						R.raw.purple_finch));
				names = new ArrayList<String>(Arrays.asList("Scarlet tanager",
						"Northern cardinal", "Blue jay", "American robin",
						"Black-capped chickadee", "Mourning dove",
						"European starling", "Rock pigeon", "House wren",
						"Song sparrow", "Cedar waxwing", "Canada goose",
						"Great blue heron", "Tundra swan", "Ring-billed gull",
						"Mallard", "Common loon", "Sandhill crane",
						"Mute swan", "Red-winged blackbird",
						"Belted kingfisher", "Osprey", "Bald eagle",
						"Barred owl", "Sharp-shinned hawk", "Peregrine falcon",
						"Northern harrier", "Snowy owl", "Great horned owl",
						"Red-tailed hawk", "Cooper's hawk",
						"Yellow-bellied sapsucker", "Downy woodpecker",
						"Pileated woodpecker", "Red-headed woodpecker",
						"Black-backed woodpecker", "Red-bellied woodpecker",
						"Hairy woodpecker", "Northern flicker", "Purple finch"));
				altNames = new ArrayList<String>(Arrays.asList(
						"scarlet tanager", "cardinal", "bluejay", "robin",
						"black capped chickadee", "mourning dove",
						"common starling", "rock dove", "house wren",
						"song sparrow", "cedar waxwing", "canada goose",
						"great blue heron", "tundra swan", "ring billed gull",
						"mallard", "loon", "sandhill crane", "mute swan",
						"red winged blackbird", "belted kingfisher", "osprey",
						"bald eagle", "barred owl", "sharp shinned hawk",
						"peregrine falcon", "northern harrier", "snowy owl",
						"great horned owl", "red tailed hawk", "coopers hawk",
						"yellow bellied sapsucker", "downy woodpecker",
						"pileated woodpecker", "red headed woodpecker",
						"black backed woodpecker", "red bellied woodpecker",
						"hairy woodpecker", "northern flicker", "purple finch"));
				
				//Randomizes the bird picture, and its respective data together
				for (int i = pics.size(); i > 0; i--) {
					int birdNum = (int) (Math.random() * i);
					pics.add(pics.remove(birdNum));
					calls.add(calls.remove(birdNum));
					names.add(names.remove(birdNum));
					altNames.add(altNames.remove(birdNum));
				}
				//Calls beginner game is beginner mode is checked off. If not, calls normal game
				if (fBeginner.isChecked()) {
					easyGame(0);
				} else {
					game(0);
				}
			}
		});
		
		//If user chooses city birds, sets picture, sounds, names and alternate names lists accordingly
		findViewById(fCity).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				pics = new ArrayList<Integer>(Arrays.asList(
						R.drawable.cardinal, R.drawable.blue_jay,
						R.drawable.robin, R.drawable.bc_chickadee,
						R.drawable.mourning_dove, R.drawable.european_starling,
						R.drawable.pigeon, R.drawable.house_wren,
						R.drawable.song_sparrow, R.drawable.cedar_waxwing));
				calls = new ArrayList<Integer>(Arrays.asList(R.raw.cardinal,
						R.raw.blue_jay, R.raw.robin, R.raw.bc_chickadee,
						R.raw.mourning_dove, R.raw.european_starling,
						R.raw.pigeon, R.raw.house_wren, R.raw.song_sparrow,
						R.raw.cedar_waxwing));
				names = new ArrayList<String>(Arrays.asList(
						"Northern cardinal", "Blue jay", "American robin",
						"Black-capped chickadee", "Mourning dove",
						"European starling", "Rock Pigeon", "House wren",
						"Song sparrow", "Cedar waxwing"));
				altNames = new ArrayList<String>(Arrays.asList("cardinal",
						"bluejay", "robin", "black capped chickadee",
						"mourning dove", "common starling", "rock dove",
						"house wren", "song sparrow", "cedar waxwing"));
				//Randomizes the bird picture, and its respective data together
				for (int i = pics.size(); i > 0; i--) {
					int birdNum = (int) (Math.random() * i);
					pics.add(pics.remove(birdNum));
					calls.add(calls.remove(birdNum));
					names.add(names.remove(birdNum));
					altNames.add(altNames.remove(birdNum));
				}
				//Calls beginner game is beginner mode is checked off. If not, calls normal game
				if (fBeginner.isChecked()) {
					easyGame(1);
				} else {
					game(1);
				}

			}

		});
		//If user chooses  wetland birds, sets picture, sounds, names and alternate names lists accordingly
		findViewById(fWetland).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				pics = new ArrayList<Integer>(Arrays.asList(R.drawable.goose,
						R.drawable.great_blue_heron, R.drawable.tundra_swan,
						R.drawable.ring_billed_gull, R.drawable.mallard,
						R.drawable.loon, R.drawable.sandhill_crane,
						R.drawable.mute_swan, R.drawable.red_winged,
						R.drawable.belted_kingfisher));
				calls = new ArrayList<Integer>(Arrays.asList(R.raw.goose,
						R.raw.great_blue_heron, R.raw.tundra_swan,
						R.raw.ring_billed_gull, R.raw.mallard, R.raw.loon,
						R.raw.sandhill_crane, R.raw.mute_swan,
						R.raw.red_winged, R.raw.belted_kingfisher));
				names = new ArrayList<String>(Arrays.asList("Canada goose",
						"Great blue heron", "Tundra swan", "Ring-billed gull",
						"Mallard", "Common loon", "Sandhill crane",
						"Mute swan", "Red-winged blackbird",
						"Belted kingfisher"));
				altNames = new ArrayList<String>(Arrays.asList("canada goose",
						"great blue heron", "tundra swan", "ring billed gull",
						"mallard", "loon", "sandhill crane", "mute swan",
						"red winged blackbird", "belted kingfisher"));
				//Randomizes the bird picture, and its respective data together
				for (int i = pics.size(); i > 0; i--) {
					int birdNum = (int) (Math.random() * i);
					pics.add(pics.remove(birdNum));
					calls.add(calls.remove(birdNum));
					names.add(names.remove(birdNum));
					altNames.add(altNames.remove(birdNum));
				}
				//Calls beginner game is beginner mode is checked off. If not, calls normal game
				if (fBeginner.isChecked()) {
					easyGame(2);
				} else {
					game(2);
				}

			}

		});
		//If user chooses Birds of Prey, sets picture, sounds, names and alternate names lists accordingly
		findViewById(fBoP).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				pics = new ArrayList<Integer>(Arrays.asList(R.drawable.osprey,
						R.drawable.bald_eagle, R.drawable.barred_owl,
						R.drawable.sharp_shinned, R.drawable.peregrine_falcon,
						R.drawable.northern_harrier, R.drawable.snowy_owl,
						R.drawable.great_horned_owl,
						R.drawable.red_tailed_hawk, R.drawable.coopers_hawk));
				calls = new ArrayList<Integer>(Arrays.asList(R.raw.osprey,
						R.raw.bald_eagle, R.raw.barred_owl,
						R.raw.sharp_shinned, R.raw.peregrine_falcon,
						R.raw.northern_harrier, R.raw.snowy_owl,
						R.raw.great_horned_owl, R.raw.red_tailed_hawk,
						R.raw.coopers_hawk));
				names = new ArrayList<String>(Arrays.asList("Osprey",
						"Bald eagle", "Barred owl", "Sharp-shinned hawk",
						"Peregrine falcon", "Northern harrier", "Snowy owl",
						"Great horned owl", "Red-tailed hawk", "Cooper's hawk"));
				altNames = new ArrayList<String>(Arrays.asList("osprey",
						"bald eagle", "barred owl", "sharp shinned hawk",
						"peregrine falcon", "northern harrier", "snowy owl",
						"great horned owl", "red tailed hawk", "coopers hawk"));
				//Randomizes the bird picture, and its respective data together
				for (int i = pics.size(); i > 0; i--) {
					int birdNum = (int) (Math.random() * i);
					pics.add(pics.remove(birdNum));
					calls.add(calls.remove(birdNum));
					names.add(names.remove(birdNum));
					altNames.add(altNames.remove(birdNum));
				}
				//Calls beginner game is beginner mode is checked off. If not, calls normal game
				if (fBeginner.isChecked()) {
					easyGame(3);
				} else {
					game(3);
				}

			}

		});
		//If user chooses woodpeckers, sets picture, sounds, names and alternate names lists accordingly
		findViewById(fWoodpeckers).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						pics = new ArrayList<Integer>(Arrays.asList(
								R.drawable.yellow_bellied, R.drawable.downy,
								R.drawable.pileated, R.drawable.red_headed,
								R.drawable.black_backed,
								R.drawable.red_bellied, R.drawable.hairy,
								R.drawable.flicker));
						calls = new ArrayList<Integer>(Arrays.asList(
								R.raw.yellow_bellied, R.raw.downy,
								R.raw.pileated, R.raw.red_headed,
								R.raw.black_backed, R.raw.red_bellied,
								R.raw.hairy, R.raw.flicker));
						names = new ArrayList<String>(Arrays.asList(
								"Yellow-bellied sapsucker", "Downy woodpecker",
								"Pileated woodpecker", "Red-headed woodpecker",
								"Black-backed woodpecker",
								"Red-bellied woodpecker", "Hairy woodpecker",
								"Northern flicker"));
						altNames = new ArrayList<String>(Arrays.asList(
								"yellow bellied sapsucker", "downy woodpecker",
								"pileated woodpecker", "red headed woodpecker",
								"black backed woodpecker",
								"red bellied woodpecker", "hairy woodpecker",
								"northern flicker"));
						//Randomizes the bird picture, and its respective data together
						for (int i = pics.size(); i > 0; i--) {
							int birdNum = (int) (Math.random() * i);
							pics.add(pics.remove(birdNum));
							calls.add(calls.remove(birdNum));
							names.add(names.remove(birdNum));
							altNames.add(altNames.remove(birdNum));
						}
						//Calls beginner game is beginner mode is checked off. If not, calls normal game
						if (fBeginner.isChecked()) {
							easyGame(4);
						} else {
							game(4);
						}

					}

				});

	}
	
	//Beginner game (with multiple choice)
	public void easyGame(final int set) {
		//Sets goMenu so that if back is pressed, menu screen is loaded
		goMenu = true;
		
		//Sets content view as easy
		setContentView(R.layout.easy);
		
		//Shows best time record in textview
		fBest = (TextView) findViewById(R.id.best);
		if (easyTimes[set] == Long.MAX_VALUE || easyTimes[set] == 0) {
			fBest.setText("Best: NONE");
			easyTimes[set] = Long.MAX_VALUE;
		} else {
			//The "%02d" adds leading zeros 
			fBest.setText("BEST: "
					+ String.format("%02d", (int) (easyTimes[set] / 60000))
					+ ":"
					+ String.format("%02d",
							Math.round((easyTimes[set] % 60000) / 1000)));
		}
		
		//Shows a timer 
		fTimer = ((Chronometer) findViewById(R.id.timerEasy));
		//Sets a base at start to calculate time at the end 
		fTimer.setBase(SystemClock.elapsedRealtime());	
		fTimer.start();
		
		//Initializes other components
		fBirdNameSpin = (Spinner) findViewById(R.id.spinner1);
		fBirdPic = (ImageView) findViewById(R.id.imageView1);
		fBirdPic.setImageResource(pics.get(0));
		fProgress = (TextView) findViewById(R.id.textView1);
		fProgress.setText("Progress: " + (i + 1) + "/" + pics.size());
		
		//Adds right answer, and two other wrong answers to spinner
		addItemsOnSpinner();
		
		//If user pressed submit...
		findViewById(R.id.submitEasy).setOnClickListener(
				new View.OnClickListener() {

					public void onClick(View v) {

						//Stops sound clip when user presses submit
						stopPlaying();
						
						int duration = Toast.LENGTH_SHORT;
						
						//Gets answer
						String birdName = String.valueOf(fBirdNameSpin
								.getSelectedItem());
						//If answer is correct...
						if (birdName.trim().equalsIgnoreCase(names.get(i))
								|| birdName.trim().equalsIgnoreCase(
										altNames.get(i))) {
							//Shows a toast saying "correct"
							Toast toast = Toast.makeText(context, "Correct!",
									duration);
							toast.show();
							//Goes to next bird
							i++;
							
							//If that was the last bird...
							if (i == pics.size()) {
								//Stops timer and calculates time taken
								fTimer.stop();
								long elapsedMillis = SystemClock
										.elapsedRealtime() - fTimer.getBase();
								
								//Makes a string of the time
								String time = (int) (elapsedMillis / 60000)
										+ ":"
										+ String.format(
												"%02d",
												Math.round((elapsedMillis % 60000) / 1000));
								
								AlertDialog.Builder builder = new AlertDialog.Builder(
										context);
								
								//If its a new best time record
								if (elapsedMillis < easyTimes[set]) {
									//Saves new time 
									easyTimes[set] = elapsedMillis;
									write();
									//Notifies user of new best time
									builder.setTitle("Congratulations!");
									builder.setMessage("You've set a new record for this category, with a time of "
											+ time + "!");
									builder.setPositiveButton(
											android.R.string.ok, null).show();
								} else {
									//If no new best time record is set, just notifies user of their time
									builder.setTitle("Congratulations!");
									builder.setMessage("You finished this category with a time of "
											+ time + "!");
									builder.setPositiveButton(
											android.R.string.ok, null).show();
								}
								
								i = 0;
								//Sets goMenu so that if back button is pressed, app closes
								goMenu = false;
								//Reloads app to go back to menu
								reload();
							}
							//Goes to next bird
							fBirdPic.setImageResource((int) pics.get(i));
							//Increments progress
							fProgress.setText("Progress: " + (i + 1) + "/"
									+ pics.size());
							//Adds new items to spinner
							addItemsOnSpinner();
						} else {
							//If answer is wrong, shows a toast
							Toast toast = Toast.makeText(context,
									"Wrong! Try again.", duration);
							toast.show();

						}

					}
				});
		
		//Plays song of the current bird when pressed
		findViewById(R.id.imageButton1).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						mp = MediaPlayer.create(getApplicationContext(),
								(int) calls.get(i));
						mp.start();
					}

				});
	}
	
	//Normal game (with textbox)
	public void game(final int set) {
		goMenu = true;
		
		//Sets content view to normal game layout
		setContentView(R.layout.activity_main);
		
		//Gets corresponding best time record
		fBest = (TextView) findViewById(R.id.best);
		if (times[set] == Long.MAX_VALUE || times[set] == 0) {
			fBest.setText("BEST: NONE");
			times[set] = Long.MAX_VALUE;
		} else {
			fBest.setText("BEST: "
					+ String.format("%02d", (int) (times[set] / 60000))
					+ ":"
					+ String.format("%02d",
							Math.round((times[set] % 60000) / 1000)));
		}
		
		//Starts timer and sets base
		fTimer = ((Chronometer) findViewById(R.id.timer));
		fTimer.setBase(SystemClock.elapsedRealtime());
		fTimer.start();
		
		//Declares other components
		fBirdName = (EditText) findViewById(R.id.editText1);
		fBirdPic = (ImageView) findViewById(R.id.imageView2);
		fBirdPic.setImageResource(pics.get(0));
		fProgress = (TextView) findViewById(R.id.textView1);
		fProgress.setText("Progress: " + (i + 1) + "/" + pics.size());
		
		//If user submits answer...
		findViewById(R.id.submit).setOnClickListener(
				new View.OnClickListener() {

					public void onClick(View v) {
						
						//Stops sound clip when user presses submit
						stopPlaying();

						int duration = Toast.LENGTH_SHORT;
						
						//Gets user's guess
						String birdName = String.valueOf(fBirdName.getText());
						//Checks if input is correct
						if (birdName.trim().equalsIgnoreCase(names.get(i))
								|| birdName.trim().equalsIgnoreCase(
										altNames.get(i))) {
							//Toasts the user saying they were correct
							Toast toast = Toast.makeText(context, "Correct!",
									duration);
							toast.show();
							i++;
							
							//If that was the last bird...
							if (i == pics.size()) {
								//Stops timer and gets time taken
								fTimer.stop();
								long elapsedMillis = SystemClock
										.elapsedRealtime() - fTimer.getBase();
								//Makes a string of the time
								String time = (int) (elapsedMillis / 60000)
										+ ":"
										+ String.format(
												"%02d",
												Math.round((elapsedMillis % 60000) / 1000));
								AlertDialog.Builder builder = new AlertDialog.Builder(
										context);
								//If a new time record is set...
								if (elapsedMillis < times[set]) {
									//Writes new record to file
									times[set] = elapsedMillis;
									write();
									//Tells user they beat the best time
									builder.setTitle("Congratulations!");
									builder.setMessage("You've set a new record for this set of Ontario Birds, with a time of "
											+ time + "!");
									builder.setPositiveButton(
											android.R.string.ok, null).show();
								} else {
									//If not, just tells user their time
									builder.setTitle("Congratulations!");
									builder.setMessage("You've finished this set of Ontario birds with a time of "
											+ time + "!");
									builder.setPositiveButton(
											android.R.string.ok, null).show();
								}
								i = 0;
								goMenu = false;
								reload();
							}
							//Goes to next bird
							fBirdPic.setImageResource((int) pics.get(i));
							fBirdName.setText("");
							//Increments progress
							fProgress.setText("Progress: " + (i + 1) + "/"
									+ pics.size());
						} else {
							//Toasts the user saying they were wrong
							Toast toast = Toast.makeText(context,
									"Wrong! Try again.", duration);
							toast.show();

						}

					}
				});

		findViewById(R.id.imageButton1).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						mp = MediaPlayer.create(getApplicationContext(),
								(int) calls.get(i));
						mp.start();
					}

				});

	}
	
	//Adds random bird names to the multiple choice spinner
	public void addItemsOnSpinner() {
		//declares variables
		int ind1 = i;
		int ind2 = i;
		ArrayList<String> list = new ArrayList<String>();
		//Gets two random indexes that do not overlap, so that there are two unique wrong choices in the spinner
		while (ind1 == i || ind1 == ind2 || ind2 == i) {
			ind1 = (int) (Math.random() * names.size());
			ind2 = (int) (Math.random() * names.size());
		}
		
		//Adds right index (i), as well as two wrong indexes to an array
		ArrayList<Integer> inds = new ArrayList<Integer>(Arrays.asList(i, ind1,
				ind2));
		
		list.add("What is the name of this bird?");
		//Adds each choice to spinner in random order
		for (int i = inds.size(); i > 0; i--) {
			int which = (int) (Math.random() * i);
			list.add(names.get((int) inds.remove(which)));
		}
		
		//Creates spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fBirdNameSpin.setAdapter(dataAdapter);
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.main, menu);
	// return true;
	// }

}
