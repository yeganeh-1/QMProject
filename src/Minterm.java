public class Minterm {
    private String number;
    private String binary;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = String.format("%s", number);
    }

    public String getBinary() {
        return binary;
    }

    public void setBinary(String binary) {
        this.binary = binary;
    }

    public int numberOfOnes() {
        int count = 0;
        for (int i = 0; i < binary.length(); i++) {
            if (binary.charAt(i) == '1') {
                count++;
            }
        }
        return count;
    }

    public String replace(int i, String s) {
        String temp = "";
        for (int j = 0; j < binary.length(); j++) {
            if (j == i) {
                temp = temp.concat(s);
                continue;
            }
            temp = temp + binary.charAt(j);
        }
        return temp;
    }

    public String distance(Minterm minterm){
        int distanceNum = -1;
        for (int i = 0; i < binary.length(); i++) {
            if (binary.charAt(i) != minterm.binary.charAt(i)) {
                if(distanceNum != -1){
                    return null;
                }
                distanceNum = i;
            }
        }
        if(distanceNum == -1){
            return null;
        }
        return replace(distanceNum, "-");
    }
}

