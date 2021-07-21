package com.damon.messenger.Adapters;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.damon.messenger.Fragments.CameraTabFragment;
import com.damon.messenger.Fragments.ChatsFragment;
import com.damon.messenger.Fragments.ContactsFragment;
import com.damon.messenger.Fragments.RequestsFragment;

public class TabsAccessorAdapter extends FragmentPagerAdapter  {
///esta clase es la encargada de crear las tablas en la parte superior la que ara de retornar
    //los fragmentos

    public TabsAccessorAdapter( FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int i) {
       //creamos un condicional swicht para valorar cada caso asiendo instanciazs entre los fragments
        //ya que evaluara y nos ara navegar en las diferentes fragmentos
        switch (i){


            case 0:
                ChatsFragment chatsFragment  =new ChatsFragment();
                return chatsFragment;
//            case 1:
//             //   GroupsFragment groupsFragment  =new GroupsFragment();
//             //   return groupsFragment;
//                UsersFragment usersFragment = new UsersFragment();
//                return  usersFragment;
            case 1:
                ContactsFragment contactsFragment  =new ContactsFragment();
                return contactsFragment;
            case 2:
                RequestsFragment requestsFragment  =new RequestsFragment();
                return requestsFragment;

            default:
                return new ChatsFragment();
        }


    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        //cramos un switch para que recorra todas la posicionbes cambiando el titulo de cas fragment
        //aqui cambianos los nombres que tendran cada tabla
        switch (position){


            case 0:
               return  "Chats";
//            case 1:
//                return  "Usuarios";
            case 1:
                return  "Contactos";
            case 2:
                return  "Solicitudes";
            default:
                return null;
        }
    }


}
