package se.kth.binyam.pairing;

import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import android.widget.Toast;

public class MainActivity extends Activity {

	public static final int REQUEST_ENABLE = 1;
	public static final int REQUEST_DISCOVERABLE = 2;

	private BluetoothAdapter bluetoothAdapter;

	private BroadcastReceiver discoveryReceiver;

	private ArrayList<BluetoothDevice> remoteDevices;
	private ArrayAdapter<BluetoothDevice> arrayAdapter;
	private ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		discoveryReceiver = new DiscoveryReceiver(); 
		registerReceiver(discoveryReceiver, new IntentFilter(
				BluetoothDevice.ACTION_FOUND));

		setContentView(R.layout.main);
		remoteDevices = new ArrayList<BluetoothDevice>();
		arrayAdapter = new ArrayAdapter<BluetoothDevice>(this,
				android.R.layout.simple_list_item_1, remoteDevices);
		listView = (ListView) findViewById(R.id.RemoteListView);
		listView.setAdapter(arrayAdapter);

		setupListenButton();
		setupSearchButton();
	}
	
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(discoveryReceiver);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_DISCOVERABLE) {
			if (resultCode > 0) { // seconds discoverable
				showToast("Your device will be discoverable for " + resultCode
						+ " seconds");
			} else {
				showToast("Your device is not discoverable");
			}
		}
	}
	
	private class DiscoveryReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			BluetoothDevice remoteDevice;
			remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			if (!bluetoothAdapter.getBondedDevices().contains(remoteDevice)) {
				remoteDevices.add(remoteDevice);
				arrayAdapter.notifyDataSetChanged();
			}
		}
	}

	private void setupListenButton() {
		Button listenButton = (Button) findViewById(R.id.ListenButton);
		listenButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Intent disc = new Intent(
						BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				startActivityForResult(disc, REQUEST_DISCOVERABLE);
			}
		});
	}

	private void setupSearchButton() {
		Button searchButton = (Button) findViewById(R.id.SearchButton);

		searchButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				registerReceiver(discoveryReceiver, new IntentFilter(
						BluetoothDevice.ACTION_FOUND));

				if (!bluetoothAdapter.isDiscovering()) {
					remoteDevices.clear();
					bluetoothAdapter.startDiscovery();
				}
			}
		});
	}

	private void showToast(String msg) {
		Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		toast.show();
	}
}