package com.ma.matthew.mygeoquiz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ma.matthew.mygeoquiz.model.Crime;
import com.ma.matthew.mygeoquiz.model.CrimeLab;

import java.util.Date;
import java.util.List;

public class CrimeListFragment extends android.support.v4.app.Fragment {

    private RecyclerView mRecyclerView;
    private CrimeAdapter mCrimeAdapter;
    private List<Crime> mCrimes;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }

    private void updateUI() {
        mCrimes = CrimeLab.get(getActivity()).getCrimes();
        if(mCrimeAdapter == null) {
            mCrimeAdapter = new CrimeAdapter(mCrimes);
            mRecyclerView.setAdapter(mCrimeAdapter);
        }else{
            mCrimeAdapter.notifyDataSetChanged();
        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;
        private Crime mCrime;

        public CrimeHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mSolvedImageView = itemView.findViewById(R.id.imageView);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(getReadableString(mCrime.getDate()));
            if(mCrime.isSolved()){
                mSolvedImageView.setVisibility(View.INVISIBLE);
            }else{
                mSolvedImageView.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onClick(View view) {
//            Toast.makeText(getActivity(),
//                    mCrime.getTitle() + " clicked!", Toast.LENGTH_SHORT)
//                    .show();
            // Start Crime Activity
            startActivity(CrimeActivity.newIntent(getActivity(),mCrime.getId()));
        }
    }

    private String getReadableString(Date date) {
        android.text.format.DateFormat dateFormat = new android.text.format.DateFormat();
        return dateFormat.format("yyyy-MM-dd",date).toString();
    }

    private class CrimePoliceViewHolder extends CrimeHolder {
        public CrimePoliceViewHolder(View itemView) {
            super(itemView);
        }
    }
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
        private List<Crime> mCrimes;
        private LayoutInflater mLayoutInflater;
        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
            mLayoutInflater = LayoutInflater.from(getActivity());
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            if(i==R.layout.list_item_crime){
                return new CrimeHolder(mLayoutInflater.inflate(R.layout.list_item_crime,viewGroup,false));
            }
            else if(i==R.layout.list_item_police_crime){
                return new CrimePoliceViewHolder(mLayoutInflater.inflate(R.layout.list_item_police_crime,viewGroup,false));
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder crimeHolder, int i) {
            Crime currentCrime = mCrimes.get(i);
            crimeHolder.bind(currentCrime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        @Override
        public int getItemViewType(int position) {
//            if(mCrimes.get(position).isRequiresPolice()){
//                return R.layout.list_item_police_crime;
//            }else{
//                return R.layout.list_item_crime;
//            }
            return R.layout.list_item_crime;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
}
