import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import com.esri.client.local.ArcGISLocalDynamicMapServiceLayer;
import com.esri.client.local.GPServiceType;
import com.esri.client.local.LocalGeoprocessingService;
import com.esri.client.local.LocalServiceStartCompleteEvent;
import com.esri.client.local.LocalServiceStartCompleteListener;
import com.esri.core.geodatabase.ShapefileFeatureTable;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry.Type;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.Graphic;
import com.esri.core.renderer.UniqueValueRenderer;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol.Style;
import com.esri.core.tasks.ags.geoprocessing.GPFeatureRecordSetLayer;
import com.esri.core.tasks.ags.geoprocessing.GPJobResource;
import com.esri.core.tasks.ags.geoprocessing.GPJobResultCallbackListener;
import com.esri.core.tasks.ags.geoprocessing.GPLinearUnit;
import com.esri.core.tasks.ags.geoprocessing.GPParameter;
import com.esri.core.tasks.ags.geoprocessing.GPServiceInfo;
import com.esri.core.tasks.ags.geoprocessing.Geoprocessor;
import com.esri.map.ArcGISDynamicMapServiceLayer;
import com.esri.map.ArcGISFeatureLayer;
import com.esri.map.ArcGISTiledMapServiceLayer;
import com.esri.map.BingMapsLayer;
import com.esri.map.BingMapsLayer.MapStyle;
import com.esri.map.FeatureLayer;
import com.esri.map.GraphicsLayer;
import com.esri.map.JMap;
import com.esri.map.MapEvent;
import com.esri.map.MapEventListenerAdapter;
import com.esri.map.MapOverlay;
import com.esri.map.OpenStreetMapLayer;
import com.esri.runtime.ArcGISRuntime;

/**
 * This sample demonstrates the use of a local geoprocessing service to
 * calculate a buffer - in this case a buffer around a point. To use
 * the sample, simply enter the buffer distance (in kilometers) and
 * left-click to add a point on the map. The clicked point will be shown and
 * a buffer of the specified distance around the point will be computed
 * and added as a graphic to the map. A LocalGeoprocessingService is
 * used to buffer the point, created from a geoprocessing package (gpk).
 */
public class FrictionRoute{


private JComponent contentPane;
  private JMap map;
  private SpatialReference srMap;
  private JProgressBar progressBar;
  private AtomicInteger tasksInProgress = new AtomicInteger(0);
  private LocalGeoprocessingService simpleBufferService;
  private Geoprocessor geoprocessor;
  private SimpleBufferExecutor simpleBufferExecutor;
  private static final String SERVICE_NAME = "Cost";
  
  private static final int PANEL_WIDTH = 230;
  private static final String FSP = System.getProperty("file.separator");
  private JButton btnGenerate;
  Graphic pointGraphic1;
  Graphic pointGraphic2;
  boolean layerAdded;
  

  // ------------------------------------------------------------------------
  // Constructors
  // ------------------------------------------------------------------------
  /**
  
   */
  public FrictionRoute() {
  }
  private void initLicense(){
	 
  }

  // ------------------------------------------------------------------------
  // Core functionality
  // ------------------------------------------------------------------------
  /**
   * Class that executes a remote geoprocessing service to calculate zones with
   * different drive times.
   */
  class SimpleBufferExecutor extends MapOverlay {

    private static final long serialVersionUID = 1L;
    JMap jMap;
    GraphicsLayer graphicsLayer;
    boolean start=true;
    boolean end=true;
    Point point1;
    Point point2;
    

    // symbology
    private final SimpleMarkerSymbol SYM_POINT = new SimpleMarkerSymbol(Color.BLACK, 14, Style.CIRCLE);
    private final SimpleMarkerSymbol SYM_POINT2 = new SimpleMarkerSymbol(Color.MAGENTA, 14, Style.CIRCLE);
    

    public SimpleBufferExecutor(JMap jMap, GraphicsLayer graphicsLayer) {
      this.jMap = jMap;
      this.graphicsLayer = graphicsLayer;
    }

    /**
     * Computes the buffer on click of the mouse.
     */
    @Override
    public void onMouseClicked(MouseEvent mouseEvent) {
    	

      if (!simpleBufferExecutor.isActive()) {
        return;
      }
      if (layerAdded){
      if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
          graphicsLayer.removeAll();
          map.getLayers().remove(2);
          layerAdded=false;
          return;
        }}
      
  
      tasksInProgress.incrementAndGet();

      // obtain the point from the mouse click
      
    if(mouseEvent.getButton() == MouseEvent.BUTTON3){	graphicsLayer.removeAll();point1=null;
    point2=null;start=true;end=true;return;
  
    }
    
      if (end&&!start){ point2 = jMap.toMapPoint(mouseEvent.getX(), mouseEvent.getY());
      end=false; }
     
     

      if(start){
      point1 = jMap.toMapPoint(mouseEvent.getX(), mouseEvent.getY());
      start=false;
     }
      // obtain the buffer distance from the text field
    

      // use the point from the mouse click and add a graphic
      pointGraphic1 = new Graphic(point1, SYM_POINT);
      graphicsLayer.addGraphic(pointGraphic1);
      pointGraphic2 = new Graphic(point2, SYM_POINT2);
      graphicsLayer.addGraphic(pointGraphic2);
     
      // execute the buffer and display the buffer zone
      
    }}

    public void executeSimpleBuffer(Graphic point1,Graphic point2) {

      // create a Geoprocessor that points to the geoprocessing service URL
    

      // initialize the required input parameters: refer to help link in the
      // geoprocessing service URL for a list of required parameters
      List<GPParameter> parameters = new ArrayList<>();

      GPFeatureRecordSetLayer pointParam1 = new GPFeatureRecordSetLayer("Input1");
      pointParam1.setGeometryType(Type.POINT);
      pointParam1.setSpatialReference(srMap);
      pointParam1.addGraphic(pointGraphic1);
      GPFeatureRecordSetLayer pointParam2 = new GPFeatureRecordSetLayer("Input2");
      pointParam2.setGeometryType(Type.POINT);
      pointParam2.setSpatialReference(srMap);
      pointParam2.addGraphic(pointGraphic2);
      

     

      parameters.add(pointParam1);
      parameters.add(pointParam2);
      geoprocessor.submitJobAndGetResultsAsync(parameters, 
    	        new String[]{"path"}, 
    	        new String[]{"path"}, 
    	        new GPJobResultCallbackListener() {

    	      @Override
    	      public void onError(Throwable e1) {
    	        System.err.println(e1.getMessage());
    	        updateProgressBarUI(null, false);
    	      }

    	      @Override
    	      public void onCallback(GPJobResource jobResource, GPParameter[] outParameters) {
    	        updateProgressBarUI(null, false);
    	        // Creates a dynamic map service layer from the returned GPJobResource and the geoprocessor URL
    	        ArcGISDynamicMapServiceLayer contourLayer = new ArcGISDynamicMapServiceLayer(geoprocessor.getUrl(), jobResource);
    	        layerAdded=map.getLayers().add(contourLayer);
    	        map.getLayers().reorderLayer(2,3);
    	    
    	        
    	        
    	      }
    	    });
    	  }
 
 

  // ------------------------------------------------------------------------
  // Static methods
  // ------------------------------------------------------------------------
  /**
   * Starting point of this application.
   * @param args arguments to this application.
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          // instance of this application
          
          FrictionRoute SimpleBuffer = new FrictionRoute();
          SimpleBuffer.initLicense();
          // create the UI, including the map, for the application
          JFrame appWindow = SimpleBuffer.createWindow();
          appWindow.add(SimpleBuffer.createUI());
          appWindow.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  // ------------------------------------------------------------------------
  // Public methods
  // ------------------------------------------------------------------------
  /**
   * Creates and displays the UI, including the map, for this application.
   * @return the UI for this sample.
   */
  public JComponent createUI() {

    // application content
    contentPane = createContentPane();

    // progress bar
    progressBar = createProgressBar(contentPane);

    // map
    map = createMap();

    // user panel
    JPanel panel = createUserPanel();

    contentPane.add(panel);
    contentPane.add(progressBar);
    contentPane.add(map);

    return contentPane;
  }

  // ------------------------------------------------------------------------
  // Private methods
  // ------------------------------------------------------------------------
  /**
   * Creates the map.
   * @return the jMap
   */
  private JMap createMap() {

    final JMap jMap = new JMap();
    // zoom to desired extent
    jMap.setShowingEsriLogo(false);
    BingMapsLayer bing = new BingMapsLayer(
    	     "AntaFrVU3y3Al1_dcP6LyqWEd-XHmGqsd6pg35HWo0BEkgC6LFiz1Na7F_iYSBa9", MapStyle.AERIAL);
    jMap.getLayers().add(bing);
    jMap.setExtent(new Envelope(-8842478.9, 5243257.87, -8691517.13, 5357641.22
));
   


    // store the spatial reference for later once the map is ready
    jMap.addMapEventListener(new MapEventListenerAdapter() {
      @Override
      public void mapReady(final MapEvent mapEvent) {
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {        
            srMap = ((JMap) mapEvent.getSource()).getSpatialReference();
          }
        });
      }
    });

    // tiled layer
    final ArcGISLocalDynamicMapServiceLayer dynamicLayer =
            new ArcGISLocalDynamicMapServiceLayer(getPathSampleData() +FSP+ "baseRaster.mpk");
    jMap.getLayers().add(dynamicLayer);
   

    // graphics layer to add point and buffer graphics
    GraphicsLayer graphicsLayer = new GraphicsLayer();
    jMap.getLayers().add(graphicsLayer);

    // create and add our custom map overlay
    simpleBufferExecutor = new SimpleBufferExecutor(jMap, graphicsLayer);
    simpleBufferExecutor.setActive(false);
    jMap.addMapOverlay(simpleBufferExecutor);

    // create and start the local geoprocessing service
    simpleBufferService = new LocalGeoprocessingService(getPathSampleData() +FSP + "Cost.gpk");
    simpleBufferService.setServiceType(GPServiceType.SUBMIT_JOB_WITH_MAP_SERVER_RESULT);
    simpleBufferService.addLocalServiceStartCompleteListener(new LocalServiceStartCompleteListener() {

      @Override
      public void localServiceStartComplete(LocalServiceStartCompleteEvent arg0) {
        // enable our map overlay which responds to button clicks
        simpleBufferExecutor.setActive(true);
        updateProgressBarUI(null, false);
        geoprocessor = new Geoprocessor(simpleBufferService.getUrlGeoprocessingService()
                + "/" + SERVICE_NAME);
            geoprocessor.setProcessSR(srMap);
            geoprocessor.setOutSR(srMap);
            btnGenerate.setEnabled(true);
      }
    });
    updateProgressBarUI("Initializing local service...", true);

    simpleBufferService.startAsync();

    return jMap;
  }


  /**
   * Creates the panel to display description and capture user input
   */
  private JPanel createUserPanel() {

    // create description
    JTextArea description = createDescription();
    Dimension preferredSize = new Dimension(PANEL_WIDTH, 30);
    // label to input buffer distance
   

    // text field to input number of days
  

    // group the above UI items into a panel
    final JPanel controlPanel = new JPanel();
    BoxLayout boxLayout = new BoxLayout(controlPanel, BoxLayout.X_AXIS);
    controlPanel.setLayout(boxLayout);
    controlPanel.setSize(PANEL_WIDTH, 20);
    controlPanel.setBorder(BorderFactory.createEmptyBorder(2,5,2,5));
    controlPanel.setBackground(new Color(255, 255, 255, 255));
    btnGenerate = new JButton("Generate");
    btnGenerate.setMinimumSize(preferredSize);
    btnGenerate.setPreferredSize(preferredSize);
    btnGenerate.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
      
        executeSimpleBuffer(pointGraphic1,pointGraphic2);
        updateProgressBarUI("Generating....", true);
        
        
       
        
      }
    });
    btnGenerate.setEnabled(false);

   
    final JPanel btnPanel = new JPanel();
    btnPanel.setLayout(new BorderLayout());
    btnPanel.add(btnGenerate, BorderLayout.NORTH);
    
    btnPanel.setBorder(null);
    btnPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

    

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout(0, 0));
    panel.setLocation(10, 10);
    panel.setSize(PANEL_WIDTH, 160);
    panel.setBackground(new Color(0, 0, 0, 0));
    panel.setBorder(new LineBorder(Color.BLACK, 3, false));

    // group control and description in a panel
    panel.add(description, BorderLayout.CENTER);
    
    panel.add(btnPanel, BorderLayout.SOUTH);

    return panel;
  }

  

/**
   * Creates a window.
   * @return a window.
   */
  private JFrame createWindow() {
    JFrame window = new JFrame("Test");
    window.setBounds(100, 100, 1000, 700);
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.getContentPane().setLayout(new BorderLayout(0, 0));
    window.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent windowEvent) {
        super.windowClosing(windowEvent);
        map.dispose();
      }
    });
    return window;
  }

  /**
   * Creates a content pane.
   * @return a content pane.
   */
  private static JLayeredPane createContentPane() {
    JLayeredPane contentPane = new JLayeredPane();
    contentPane.setBounds(100, 100, 1000, 700);
    contentPane.setLayout(new BorderLayout(0, 0));
    contentPane.setVisible(true);
    return contentPane;
  }

  /**
   * Creates a progress bar.
   * @param parent progress bar's parent. The horizontal axis of the progress bar will be
   * center-aligned to the parent.
   * @return a progress bar.
   */
  private static JProgressBar createProgressBar(final JComponent parent) {
    final JProgressBar progressBar = new JProgressBar();
    progressBar.setSize(260, 20);
    parent.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        progressBar.setLocation(
            parent.getWidth()/2 - progressBar.getWidth()/2,
            parent.getHeight() - progressBar.getHeight() - 20);
      }
    });
    progressBar.setStringPainted(true);
    progressBar.setIndeterminate(true);
    return progressBar;
  }

  /**
   * Updates progress bar UI from Swing's Event Dispatch Thread.
   * @param str string to be set.
   * @param visible flag to indicate visibility of the progress bar.
   */
  private void updateProgressBarUI(final String str, final boolean visible) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        if (str != null) {
          progressBar.setString(str);
        }
        progressBar.setVisible(visible);
      }
    }); 
  }

  /**
   * Creates description UI component.
   * @return description.
   */
  private JTextArea createDescription() {
    JTextArea description = new JTextArea(
        "Left click on the map to create the start point(black) and the destination(pink) and automatically " +
            "compute the shortest cost route."+"Right click to remove." 
        );
    description.setFont(new Font("Verdana", Font.PLAIN, 11));
    description.setForeground(Color.WHITE);
    description.setBackground(new Color(0, 0, 0, 180));
    description.setEditable(false);
    description.setLineWrap(true);
    description.setWrapStyleWord(true);
    description.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    return description;
  }

  private String getPathSampleData() {
    String dataPath = null;
    String folder=System.getProperty("user.dir");
    dataPath=folder+FSP+"data";
    return dataPath;
    
  }
}
