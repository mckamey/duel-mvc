package com.example.mvcapp.controllers;

import org.duelengine.duel.mvc.Apply;
import org.duelengine.duel.mvc.DuelController;

import com.example.mvcapp.aspects.LatencyTimer;

/**
 * Base for all controllers in app
 */
@Apply({ LatencyTimer.class })
public abstract class BaseController extends DuelController {

}
