package com.coe.project.codeit.CodingPage;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavAction;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.coe.project.codeit.BluetoothConnection.SelectDeviceActivity;
import com.coe.project.codeit.FirstFragment;
import com.coe.project.codeit.MainActivity;
import com.coe.project.codeit.MarkerDetector.CodeScanner;
import com.coe.project.codeit.R;

import static com.coe.project.codeit.BluetoothConnection.CommandControl.connectedThread;


public class RecyclerDragdropActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;

    ArrayList<String> cmdlist;
    ArrayList<String> cmd_temp;
    ArrayList<String> IncomingCmd;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showcmd_fragment);
        //incoming cmd
        Intent intent = getIntent();
        IncomingCmd = (ArrayList<String>) intent.getSerializableExtra("cmdlist");
        System.out.println(IncomingCmd);

        if (IncomingCmd == null && cmdlist == null) {
            cmdlist = new ArrayList<>();
            System.out.println("loop 1");
        } else if (IncomingCmd != null && cmdlist == null){
            cmd_temp = IncomingCmd;
            cmdlist = new ArrayList<>();
            cmd_temp.forEach((temp) -> {
                System.out.println(temp);
                switch (temp) {
                    case "mf":
                        cmdlist.add("mf");
                        break;
                    case "mb":
                        cmdlist.add("mb");
                        break;
                    case "ml":
                        cmdlist.add("ml");
                        break;
                    case "mr":
                        cmdlist.add("mr");
                        break;
                }
            });
        }

        int level = intent.getIntExtra("level", 0);

        TextView level_text = (TextView) findViewById(R.id.level);
        level_text.setText("Level " + level);

        ImageView level_map = (ImageView) findViewById(R.id.level_map);
        switch (level) {
            case 1:
                level_map.setImageResource(R.drawable.level1_map);
                break;
            case 2:
                level_map.setImageResource(R.drawable.level2_map);
                break;
            case 3:
                level_map.setImageResource(R.drawable.level3_map);
                break;
            case 4:
                level_map.setImageResource(R.drawable.level4_map);
                break;
            case 5:
                level_map.setImageResource(R.drawable.level5_map);
                break;
        }

        ImageButton forward_btn = (ImageButton) findViewById(R.id.forward_btn);
        ImageButton right_btn = (ImageButton) findViewById(R.id.right_btn);
        ImageButton backward_btn = (ImageButton) findViewById(R.id.backward_btn);
        ImageButton left_btn = (ImageButton) findViewById(R.id.left_btn);
        ImageButton reset_btn = (ImageButton) findViewById(R.id.reset_btn);
        ImageButton run_btn = (ImageButton) findViewById(R.id.run_btn);
        ImageButton camera_btn = (ImageButton) findViewById(R.id.camera_btn);
        ImageButton back_button = (ImageButton) findViewById(R.id.back_button);
        //TextView cmdlist_show = (TextView) findViewById(R.id.cmdlist_show);

        forward_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cmdlist.add("mf");
                recyclerAdapter.notifyDataSetChanged();
                cmd_temp = cmdlist;
                //cmdlist_show.setText(cmdlist.toString());
            }
        });

        backward_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cmdlist.add("mb");
                recyclerAdapter.notifyDataSetChanged();
                cmd_temp = cmdlist;
                //cmdlist_show.setText(cmdlist.toString());
            }
        });

        right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cmdlist.add("mr");
                recyclerAdapter.notifyDataSetChanged();
                cmd_temp = cmdlist;
                //cmdlist_show.setText(cmdlist.toString());
            }
        });

        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cmdlist.add("ml");
                recyclerAdapter.notifyDataSetChanged();
                cmd_temp = cmdlist;
                //cmdlist_show.setText(cmdlist.toString());
            }
        });

        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cmdlist.clear();
                recyclerAdapter.notifyDataSetChanged();
                //cmdlist_show.setText(cmdlist.toString());
            }
        });

        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecyclerDragdropActivity.this, CodeScanner.class);
                intent.putExtra("cmdlist", (Serializable) cmdlist);
                intent.putExtra("level", (Integer) level);
                startActivity(intent);
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecyclerDragdropActivity.this, Levelselect.class);
                startActivity(intent);
            }
        });

        run_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cmd = "";
                for (String s : cmdlist)
                {
                    cmd += s+",";
                }
                if (cmd != null && cmd.length() > 0) {
                    cmd = cmd.substring(0, cmd.length() - 1);
                }
                System.out.println(cmd);
                try{
                    connectedThread.write(cmd);
                } catch (Exception ex) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecyclerDragdropActivity.this);
                    builder.setMessage("ยังไม่ได้เชื่อมต่อบลูทูธ ไปที่หน้าเชื่อมต่อ");
                    builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(RecyclerDragdropActivity.this, SelectDeviceActivity.class);
                            startActivity(intent);
                        }
                    });
                    builder.show();
                }
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        recyclerAdapter = new RecyclerAdapter(cmdlist);
        recyclerView.setAdapter(recyclerAdapter);
        Drawable mDivider = ContextCompat.getDrawable(this, R.drawable.divider);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(mDivider);
        recyclerView.addItemDecoration(dividerItemDecoration);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            //TextView cmdlist_show = (TextView) findViewById(R.id.cmdlist_show);

            Collections.swap(cmdlist, fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            //cmdlist_show.setText(cmdlist.toString());
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }

    };

}