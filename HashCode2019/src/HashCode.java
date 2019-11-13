import java.io.*;
import java.util.*;

class Foto {

    private char pos;
    private int num;
    private String fotoNumber;
    private ArrayList<String> tags;
    public int score;

    //build a photo with own score
    Foto(Foto f, int score){
        this.pos=f.pos;
        this.num=f.num;
        this.fotoNumber=f.fotoNumber;
        this.tags=f.tags;
        this.score=score;
    }

    //build a photo with string x that contains [pos, num, tags, score] and string n that is photoNumber
    Foto(String x, String n) {
        this.score=0;
        tags=new ArrayList<>();
        String[] a=x.split(" ");
        this.pos = a[0].charAt(0);
        this.fotoNumber = n;
        this.num=Integer.parseInt(a[1]);
        for(int i=2;i<a.length;i++)
            this.tags.add(a[i]);
    }

    //duplicate a photo with another photo
    Foto(Foto a, Foto b){
        this.score=0;
        ArrayList<String> aT=a.getTags();
        ArrayList<String> bT=b.getTags();
        for(int i = 0; i < aT.size();i++)
            if(!bT.contains(aT.get(i)))
                bT.add(aT.get(i));
        this.tags=bT;
        this.num = this.tags.size();
        this.fotoNumber = a.getFotoNumber() + " " + b.getFotoNumber();
    }

    public char getPos() {
        return pos;
    }

    public String getFotoNumber() {
        return fotoNumber;
    }

    public int getNum() {
        return num;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    @Override
    public boolean equals(Object obj) {
        Foto f=(Foto)obj;
        return f.fotoNumber.equals(this.fotoNumber);
    }
}


public class HashCode {
    //find how many tags are in both photos
    public static int intersezione(ArrayList<String> a,ArrayList<String> b){
        if(a.size()>0 && b.size()>0) {
            int n=0;
            for(int i=0;i<a.size();i++)
                if(b.contains(a.get(i)))
                    n++;
            return n;
        }
        return 0;
    }

    //return the minimum of(aTags-bTags, bTags-aTags, intersectionOf(aTags, bTags))
    public static int score(Foto a, Foto b){
        int inter = intersezione(a.getTags(),b.getTags());
        int partea = a.getTags().size() - inter;
        int parteb = b.getTags().size() - inter;
        return Math.min(inter,Math.min(partea,parteb));
    }
    public static void main(String[] args) throws IOException {

        //open the file in read mode
        FileReader f=new FileReader("c_memorable_moments.txt");
        BufferedReader in=new BufferedReader(f);

        //catch the number of photos
        int n=Integer.parseInt(in.readLine());

        //new Foto array for storing them
        Foto [] fo= new Foto[n];

        //put the Photos into the array
        for (int i=0;i<n;i++)  fo[i] = new Foto(in.readLine(),Integer.toString(i));

        //fast in reading
        ArrayList<Foto> ver=new ArrayList<>();

        //fast in inserting and deleting
        LinkedList<Foto> hor=new LinkedList<>();

        //put all vertical photos into ver and all horizontal photos into hor
        for(int i = 0 ; i < fo.length;i++) {
            if(fo[i].getPos() == 'H') hor.add(fo[i]); else ver.add(fo[i]);
        }

        //put all vertical photos into hor grouping them in couples
        for (int i = 0;i< ver.size()-1;i=i+2){
            hor.add(new Foto(ver.get(i),ver.get(i+1)));
        }

        String ris = (hor.size())+"\n";
        StringBuilder sb=new StringBuilder(ris);
        int l = hor.size();
        //organize all the photos comparing their score with a parallel stream
        for(int i=0;i<l-1;i++){
            Foto fuori=hor.pop();

            sb.append(fuori.getFotoNumber()).append("\n");
            
            Optional<Foto> f1 = hor.parallelStream().map(
                    foto->
                        new Foto(foto,score(fuori,foto)))
                    .reduce(
                        (a,b)->{
                            if(a.score>b.score)
                                return a;
                            return b;
                        }
                    );
            if(f1.isPresent()){
                Foto t=hor.remove(hor.indexOf(f1.get()));
                hor.add(0,t);
            }
        }

        Foto fuori=hor.pop();
        sb.append(fuori.getFotoNumber()).append("\n");

        //open the output.txt file in write mode
        FileWriter w=new FileWriter("output.txt");
        BufferedWriter out=new BufferedWriter(w);

        //write the output and close the file
        out.write(sb.toString());
        out.close();
    }
}
