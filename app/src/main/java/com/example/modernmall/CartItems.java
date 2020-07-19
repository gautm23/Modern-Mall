package com.example.modernmall;

public class CartItems {
    public String actualQuantity;
    public String firebaseAddressShopping;
    public String firebaseAddressInventory;
    public String price;
    public String purchasedValue;
    public String quantityInCart;

   public CartItems (){
    };

  public  CartItems(String actualQuantity, String firebaseAddressShopping,String firebaseAddressInventory, String price, String purchasedValue, String quantityInCart )
    {
        this.actualQuantity=actualQuantity;
        this.firebaseAddressShopping=firebaseAddressShopping;
        this.firebaseAddressInventory=firebaseAddressInventory;
        this.price=price;
        this.purchasedValue=purchasedValue;
        this.quantityInCart=quantityInCart;
    }
}
