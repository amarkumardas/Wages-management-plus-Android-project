package amar.das.acbook.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import amar.das.acbook.R;
import amar.das.acbook.activity.IndividualPersonDetailActivity;
import amar.das.acbook.model.SearchModel;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> implements Filterable {
    Context contex;
    ArrayList<SearchModel> arrayList ;
    ArrayList<SearchModel> backup  ;


    public SearchAdapter(Context context, ArrayList<SearchModel> datalist) {
        this.contex = context;
        this.arrayList = datalist;
        backup=new ArrayList<>(datalist);//storing object to backup arraylist

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_search_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       SearchModel data=arrayList.get(position);


       holder.id.setText("ID: "+data.getId());
       holder.name.setText(HtmlCompat.fromHtml("NAME:"+"<b>"+data.getName()+"</b>",HtmlCompat.FROM_HTML_MODE_LEGACY));
       holder.father.setText("FATHER:"+data.getFather());
       //user may enter only account no or aadhar so if else is use separately
       //account and aadhar length should be greater than 4 or 5 otherwise stringoutofboundexception because we r using this method (data.getAccount().length() - 4 or 5)so checking in if statement.we are viewing last 4 and 5 letters to user
        if(data.getAccount().length()>4  ) {
            holder.account.setText(HtmlCompat.fromHtml("A/C:____" +"<b>"+ data.getAccount().substring(data.getAccount().length() - 4)+"<b>",HtmlCompat.FROM_HTML_MODE_LEGACY));//getting last 4 letters
        }else {/**when data is not there in Db than set account  to - otherwise others people value is been showed in place of account To check just comment next line and see.Default null in databse is not working only empty data is set if user dont enter data.*/
            holder.account.setText("A/C:    -");
          }

        if(data.getAadhar().length()>5){
            holder.aadhar.setText(HtmlCompat.fromHtml("AADHAR:____" +"<b>"+ data.getAadhar().substring(data.getAadhar().length() - 5)+"<b>",HtmlCompat.FROM_HTML_MODE_LEGACY));//getting last 5 letters
        }else{/**when data is not there in Db than set  aadhar to - otherwise others people value is been showed in place of aadhar .Default null in databse is not working only empty data is set if user dont enter data*/
            holder.aadhar.setText("AADHAR:    -");
         }

       holder.singleRowCartView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent=new Intent(contex, IndividualPersonDetailActivity.class);
               intent.putExtra("ID",data.getId());
               contex.startActivity(intent);
           }
       });
    }

    @Override
    public int getItemCount() {
        return  arrayList.size();
    }

    @Override
    public Filter getFilter() {
          return filter;//we have to create anonymous filter class
    }

    //it works background can be called as child thread
    Filter filter=new Filter() {//anonymous filter class

        @Override
        protected FilterResults performFiltering(CharSequence keyword) {//charSequence changet to keyword
            ArrayList<SearchModel> filteredData=new ArrayList<>();//whatever data is filtered will be store here


            if(keyword.toString().matches("[\\s*[0-9]+\\s*]+")) {//\\s is whitespace and * is optional if user enter space between caharacter then also work

                String str=keyword.toString().replaceAll(" ","");//removing all spaces with ""

                if (str.length() == 4) {
                     for (SearchModel obj : backup) {
                       //contains method checks like pattern match
                       // if (obj.getAccount().substring(obj.getAccount().length()-4).equals(str)) //not working while using only this statement no idea
                           if(obj.getAccount().contains(str)) {//if it matches then store in filtereddtaa ie. arraylist
                               if (obj.getAccount().substring(obj.getAccount().length()-4).equals(str))//boilerplate code but its working
                                   filteredData.add(obj);//equals method check  by content
                           }
                    }
                }
                else if (str.length() == 5) {
                    // if (obj.getAadhar().substring(obj.getAadhar().length()-5).equals(str))//not working while using only this statement no idea
                    for (SearchModel obj : backup) {
                        if (obj.getAadhar().contains(str)) {//if it matches then store in filtereddtaa ie. arraylist
                            //here already length is checked so no exception obj.getAadhar().length()-5
                            if (obj.getAadhar().substring(obj.getAadhar().length()-5).equals(str))//boilerplate code but its working
                               filteredData.add(obj);//equals method check  by content
                        }
                    }
                }
            }else if(keyword.toString().matches("[\\s*[a-zA-Z]+\\s*]+")){
               // if(obj.getName().contain(keyword.toString().toUpperCase().trim())) if we use this then name may be duplicate so using contain method so that it store when it match like patter match
                for (SearchModel obj : backup) {             //front and last spaces are removed using trim()
                    if(obj.getName().startsWith(keyword.toString().toUpperCase().trim()))//if we enter A the it will show result whose first letter is A followed by other letters so we will get exact name
                    {//if it matches then store in filtereddtaa ie arraylist
                        filteredData.add(obj);
                    }
                }


            }else if(keyword.toString().matches("[\\s*[a-zA-Z][0-9]+\\s*]+")){//to search with id user have to type any letter then id

                String str=keyword.toString().replaceAll("[\\s*[a-zA-Z]+\\s]","");//replacing all spaces and alphabat with ""

                for (SearchModel obj : backup) {
                    //since id is not duplicate so filteredData should not contain duplicate like 23,123,123 here 23 is in all so
                     // equal method is used to check only content and then store
                   if(obj.getId().equals(str))//if it matches then store in filtereddtaa ie arraylist
                    {  //string equals( ) method is called because obj.getId() is string
                        filteredData.add(obj);
                        break;//once id found then break because id is not duplicate
                    }
                }
            }

            //we have to return FilterResults so creating its object
            FilterResults result=new FilterResults();
            result.values=filteredData;
            return result; //this return will go in publishResults method
        }

        @Override //main UI thread
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
              arrayList.clear();//clearing to show new filter search result
              arrayList.addAll( (ArrayList<SearchModel>) filterResults.values);//typecasting to arraylist because
              notifyDataSetChanged();//when background child thread searching is completed then this method will notify to main UI thread that search is completed
        }
    };//it is like statement so ; is necessary

    public class ViewHolder extends  RecyclerView.ViewHolder{
        TextView name,id,aadhar,account,father;
        CardView singleRowCartView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
           name=itemView.findViewById(R.id.search_name_tv);
           id=itemView.findViewById(R.id.search_id_tv);
           aadhar=itemView.findViewById(R.id.search_aadhar_tv);
           account=itemView.findViewById(R.id.search_ac_tv);
           father=itemView.findViewById(R.id.search_father_tv);
           singleRowCartView=itemView.findViewById(R.id.single_row_cartview);

        }
    }
}
