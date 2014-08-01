package br.law123.collide;

/**
 * Referencia para numero.
 * 
 * @author teixeira
 */
public class NumberReference {

    private Number number;

    public NumberReference(Number number) {
        this.number = number;
    }

    public Number get() {
        return number;
    }

    public void set(Number number) {
        this.number = number;
    }

}
