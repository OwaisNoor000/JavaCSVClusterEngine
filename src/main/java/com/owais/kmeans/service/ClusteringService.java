package com.owais.kmeans.service;


import com.owais.kmeans.model.Product;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.csv.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

@Service
public class ClusteringService {

    private final int EPOCHS = 10;
    private final int SAMPLE_SIZE = 20;
    private final List<Integer> K_VALUES = List.of(3, 4, 5);

    private SentenceEncoder encoder;

    public void init() {
        encoder = new SentenceEncoder();
    }

    public List<Map<String, Object>> cluster(List<Product> products) throws IOException {

        Collections.shuffle(products);
        products = products.subList(0, SAMPLE_SIZE);

        List<Map<String, double[]>> encodedProducts = new ArrayList<>();
        for (Product product : products) {
            double[] nameEmbedding = encoder.encode(product.getProdName());
            double[] typeEmbedding = encoder.encode(product.getProductTypeName());
            double[] colorEmbedding = encoder.encode(product.getColourName());

            Map<String, double[]> embedding = new HashMap<>();
            embedding.put("name", nameEmbedding);
            embedding.put("category", typeEmbedding);
            embedding.put("color", colorEmbedding);
            encodedProducts.add(embedding);
        }

        List<Map<String, Object>> WCVs = new ArrayList<>();

        for (int K : K_VALUES) {
            List<Map<String, double[]>> centers = kMeansPlusPlusInit(encodedProducts, K);

            for (int epoch = 0; epoch < EPOCHS; epoch++) {
                List<List<Map<String, double[]>>> clusters = new ArrayList<>();
                for (int i = 0; i < K; i++) clusters.add(new ArrayList<>());

                for (Map<String, double[]> point : encodedProducts) {
                    int closest = findClosestCenter(point, centers);
                    clusters.get(closest).add(point);
                }

                centers = recomputeCenters(clusters);

                if (epoch == EPOCHS - 1) {
                    double wcv = computeWithinClusterVariance(clusters, centers);
                    Map<String, Object> result = new HashMap<>();
                    result.put("Cluster", K);
                    result.put("Variance", wcv);
                    WCVs.add(result);
                }
            }
        }

        // print WCVs for logging
        for (Map<String, Object> wc : WCVs) {
            System.out.println("K=" + wc.get("Cluster") + " Variance=" + wc.get("Variance"));
        }
        
        return WCVs;
    }

    private List<Map<String, double[]>> kMeansPlusPlusInit(List<Map<String, double[]>> data, int K) {
        List<Map<String, double[]>> centers = new ArrayList<>();
        centers.add(data.get(new Random().nextInt(data.size())));

        while (centers.size() < K) {
            double maxProb = -1;
            Map<String, double[]> bestPoint = null;

            for (Map<String, double[]> point : data) {
                double minDist = Double.MAX_VALUE;
                for (Map<String, double[]> center : centers) {
                    double dist = calcDistance(point, center);
                    if (dist < minDist) minDist = dist;
                }
                if (minDist > maxProb) {
                    maxProb = minDist;
                    bestPoint = point;
                }
            }
            if (bestPoint != null) centers.add(bestPoint);
        }
        return centers;
    }

    private int findClosestCenter(Map<String, double[]> point, List<Map<String, double[]>> centers) {
        double minDist = Double.MAX_VALUE;
        int bestIndex = 0;
        for (int i = 0; i < centers.size(); i++) {
            double dist = calcDistance(point, centers.get(i));
            if (dist < minDist) {
                minDist = dist;
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    private List<Map<String, double[]>> recomputeCenters(List<List<Map<String, double[]>>> clusters) {
        List<Map<String, double[]>> newCenters = new ArrayList<>();

        for (List<Map<String, double[]>> cluster : clusters) {
            double[][] nameVecs = new double[cluster.size()][];
            double[][] catVecs = new double[cluster.size()][];
            double[][] colVecs = new double[cluster.size()][];

            for (int i = 0; i < cluster.size(); i++) {
                nameVecs[i] = cluster.get(i).get("name");
                catVecs[i] = cluster.get(i).get("category");
                colVecs[i] = cluster.get(i).get("color");
            }

            Map<String, double[]> center = new HashMap<>();
            center.put("name", averageVectors(nameVecs));
            center.put("category", averageVectors(catVecs));
            center.put("color", averageVectors(colVecs));

            newCenters.add(center);
        }

        return newCenters;
    }

    private double[] averageVectors(double[][] vectors) {
        int len = vectors[0].length;
        double[] avg = new double[len];
        for (double[] vec : vectors) {
            for (int i = 0; i < len; i++) avg[i] += vec[i];
        }
        for (int i = 0; i < len; i++) avg[i] /= vectors.length;
        return avg;
    }

    private double calcDistance(Map<String, double[]> p1, Map<String, double[]> p2) {
        double nameDist = cosineDistance(p1.get("name"), p2.get("name"));
        double catDist = cosineDistance(p1.get("category"), p2.get("category"));
        double colorDist = cosineDistance(p1.get("color"), p2.get("color"));
        return (nameDist + catDist + colorDist) / 3.0;
    }

    private double cosineDistance(double[] a, double[] b) {
        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return 1 - (dot / (Math.sqrt(normA) * Math.sqrt(normB)));
    }

    private double computeWithinClusterVariance(List<List<Map<String, double[]>>> clusters, List<Map<String, double[]>> centers) {
        double total = 0.0;
        for (int i = 0; i < clusters.size(); i++) {
            for (Map<String, double[]> point : clusters.get(i)) {
                double dist = calcDistance(point, centers.get(i));
                total += dist * dist;
            }
        }
        return total;
    }
}

