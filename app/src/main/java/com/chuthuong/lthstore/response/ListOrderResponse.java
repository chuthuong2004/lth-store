package com.chuthuong.lthstore.response;

import com.chuthuong.lthstore.model.Order;

import java.io.Serializable;
import java.util.List;

public class ListOrderResponse  implements Serializable {
        private List<Order> orders;
        private boolean success;
        private  int countDocuments;
        private int resultPerPage;



        public List<Order> getOrders() {
            return orders;
        }

        public void setOrders(List<Order> orders) {
            this.orders = orders;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public int getCountDocuments() {
            return countDocuments;
        }

        public void setCountDocuments(int countDocuments) {
            this.countDocuments = countDocuments;
        }

        public int getResultPerPage() {
            return resultPerPage;
        }

        public void setResultPerPage(int resultPerPage) {
            this.resultPerPage = resultPerPage;
        }

        public ListOrderResponse(List<Order> orders, boolean success, int countDocuments, int resultPerPage) {
            this.orders = orders;
            this.success = success;
            this.countDocuments = countDocuments;
            this.resultPerPage = resultPerPage;
        }
        @Override
        public String toString() {
            return "ListOrderResponse{" +
                    "orders=" + orders +
                    ", success=" + success +
                    ", countDocuments=" + countDocuments +
                    ", resultPerPage=" + resultPerPage +
                    '}';
        }
}
