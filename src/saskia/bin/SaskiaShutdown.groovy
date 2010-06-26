package saskia.bin

class SaskiaShutdown extends Thread {

    def r
   
    public SaskiaShutdown(r) {
		super()
		this.r=r
    }
    
    public void run()  {	
		r.abort()
    }
}