package com.example.mvcapp.controllers;

import org.duelengine.duel.mvc.Apply;
import org.duelengine.duel.mvc.DuelController;

import com.example.mvcapp.aspects.LatencyFilter;

/**
 * Base for all controllers in app
 */
@Apply({ LatencyFilter.class })
public abstract class BaseController extends DuelController {

}
