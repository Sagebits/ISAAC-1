package sh.isaac.api.component.semantic;

/**
 * 
 * {@link SemanticBuildListener}
 *
 * @author <a href="mailto:joel.kniaz.list@gmail.com">Joel Kniaz</a>
 *
 */
public abstract class SemanticBuildListener implements SemanticBuildListenerI {
   private boolean enabled = true;

   public SemanticBuildListener() {
   }
   
   /* (non-Javadoc)
    * @see gov.vha.isaac.ochre.api.component.sememe.SememeBuildListenerI#getListenerName()
    */
   @Override
   public String getListenerName() {
      return getClass().getSimpleName();
   }

   /* (non-Javadoc)
    * @see gov.vha.isaac.ochre.api.component.sememe.SememeBuildListenerI#enable()
    */
   @Override
   public void enable() {
      enabled = true;
   }

   /* (non-Javadoc)
    * @see gov.vha.isaac.ochre.api.component.sememe.SememeBuildListenerI#disable()
    */
   @Override
   public void disable() {
      enabled = false;
   }

   /* (non-Javadoc)
    * @see gov.vha.isaac.ochre.api.component.sememe.SememeBuildListenerI#isEnabled()
    */
   @Override
   public boolean isEnabled() {
      return enabled;
   }
}
