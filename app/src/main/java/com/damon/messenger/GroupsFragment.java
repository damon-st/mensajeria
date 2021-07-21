package com.damon.messenger;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.damon.messenger.Activitys.GroupChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {


    private View groupFragmentView;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;//creamos un adaptador
    private ArrayList<String>list_of_groups = new ArrayList<>();

    private DatabaseReference GropuRef;

    private List<String> followingList;


    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFragmentView =  inflater.inflate(R.layout.fragment_groups, container, false);

        //aqui estamos rescatando los grupos que tenemos en firebase con este metodo donde dice child
        //ponemos el nombre del grupo que esta creado
        GropuRef = FirebaseDatabase.getInstance().getReference().child("Gropus");

        InitializeFields();


        checkFollowing();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String curretGroupName = parent.getItemAtPosition(position).toString();

                Intent groupChatIntent = new Intent(getContext(), GroupChatActivity.class);
                groupChatIntent.putExtra("groupName",curretGroupName);//aqui mandamos datos a gropuchatactivity
                startActivity(groupChatIntent);

            }
        });


        return  groupFragmentView;
    }

    private void RetriveAndDisplayGropus() {

        GropuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<>();// este metodo se usa para crear las listas que ay de grupos
                Iterator iterator  = dataSnapshot.getChildren().iterator();

                while (iterator.hasNext()){
                   set.add(((DataSnapshot)iterator.next()).getKey());
                }

                list_of_groups.clear();
                list_of_groups.addAll(set);//aqui se añade la lista set que creamos arriba
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void checkFollowing(){
        followingList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Contacts")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                ;

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }
                // readPosts();
                RetriveAndDisplayGropus();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void InitializeFields() {

        listView = groupFragmentView.findViewById(R.id.list_view);
        //mandamos el contexto y buscamos una vista y mandamso el array de la lista
        arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,list_of_groups);
        listView.setAdapter(arrayAdapter);//aqui en la listview mandamnos el adaptador que creamos arriba
    }

}
