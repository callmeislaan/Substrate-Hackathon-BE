package com.backend.apiserver.utils;

import com.backend.apiserver.entity.RatingBean;
import com.google.common.collect.Lists;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import java.util.HashMap;
import java.util.List;

public class CFUtils {
    public static HashMap<Long, Float> recommend(List<RatingBean> ratingBeans, long userIdSrc, int number) throws TasteException {
        HashMap<Long, Float> currentUserRatingHashMap = new HashMap<>();

        //create face by id map
        FastByIDMap<PreferenceArray> userData = new FastByIDMap<>();
        List<Preference> row = Lists.newArrayList();
        long userIdTemp = 0;
        for (RatingBean ratingBean : ratingBeans) {
            long userId = ratingBean.getUserId();
            long mentorId = ratingBean.getMentorId();
            float rating = ratingBean.getRating();
            if (userIdTemp != 0 && userId != userIdTemp) {
                userData.put(userIdTemp, new GenericUserPreferenceArray(row));
                row = Lists.newArrayList();
            }
            row.add(new GenericPreference(userId, mentorId, rating));
            userIdTemp = userId;

            if (userIdSrc == userId) {
                currentUserRatingHashMap.put(mentorId, rating * 10);
            } else {
//                if (!currentUserRatingHashMap.containsKey(mentorId)) {
//                    currentUserRatingHashMap.put(mentorId, 0f);
//                }
            }
        }
        userData.put(userIdTemp, new GenericUserPreferenceArray(row));

        //create data model
        DataModel model = new GenericDataModel(userData);

        //create similarity
        TanimotoCoefficientSimilarity similarity = new TanimotoCoefficientSimilarity(model);

        //item base algorithm
        ItemBasedRecommender recommender = new GenericItemBasedRecommender(model, similarity);

        //recommend
        List<RecommendedItem> recommendedItems = recommender.recommend(userIdSrc, number);

        for (RecommendedItem recommendation : recommendedItems) {
//            System.out.println(recommendation.getItemID() + " - " + recommendation.getValue());
            currentUserRatingHashMap.put(recommendation.getItemID(), recommendation.getValue());
        }

        return currentUserRatingHashMap;
    }
}
