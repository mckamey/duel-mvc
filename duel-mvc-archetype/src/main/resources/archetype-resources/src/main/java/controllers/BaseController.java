#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.controllers;

import org.duelengine.duel.mvc.Apply;
import org.duelengine.duel.mvc.DuelController;

import ${package}.aspects.CustomResponseHeaders;
import ${package}.aspects.LatencyTimer;

/**
 * Base for all controllers in app
 */
@Apply({ CustomResponseHeaders.class, LatencyTimer.class })
public abstract class BaseController extends DuelController {

}
