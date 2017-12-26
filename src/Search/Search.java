package Search;

import Step3.Database;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



@WebServlet("/Search")
public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Database database;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Search() {
        super();
//        database = new Database(9998,9999);	
//		database.start();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Map<String, String[]> parameters = request.getParameterMap();	
		if (parameters.containsKey("query")) {
			long startTime = System.currentTimeMillis();
			String query = request.getParameter("query").toLowerCase();
			 Database database = new Database();
			 ArrayList<String> result = database.searchLocal(query);
			
			// tcpconnection part
//			Database database = new Database(9998,9999);			
//			database.start();
//			ArrayList<String> result = database.search(query);
			
			
			long endTime=System.currentTimeMillis(); 
			long timeused = endTime-startTime;
			String timeUsed = Long.toString(timeused/1000)+"."+Long.toString(timeused%1000);
			request.setAttribute("query",request.getParameter("query"));
			request.setAttribute("timeUsed",timeUsed );
			request.setAttribute("result", result);
			RequestDispatcher rd = request.getRequestDispatcher("result.jsp");
			rd.forward(request, response);
			
		}
		//request.getRequestDispatcher("result.jsp").forward(request, response);
		

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

