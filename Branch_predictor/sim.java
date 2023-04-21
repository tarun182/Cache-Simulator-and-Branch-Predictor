public class sim
{
	public static void main(String[] args) { 
        if (args[0].equals("smith")){
            smith obj = new smith();
            obj.smith_bp(args);
        }
        else if (args[0].equals("bimodal")){
            bimodal obj = new bimodal();
            obj.bimodal_bp(args);
        }
        else if (args[0].equals("gshare")){
            gshare obj = new gshare();
            obj.gshare_bp(args);
        }
        else if (args[0].equals("hybrid")){
            hybrid obj = new hybrid();
            obj.hybrid_bp(args);
        }

        else {
            System.out.println("Invalid Input");
        }

	}
}
