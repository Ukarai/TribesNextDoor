package me.theguynextdoor.tribesnextdoor.backends;

import me.theguynextdoor.tribesnextdoor.datatypes.civilisation.Civilisation;
import me.theguynextdoor.tribesnextdoor.datatypes.resident.Resident;
import me.theguynextdoor.tribesnextdoor.datatypes.tribe.Tribe;
import me.theguynextdoor.tribesnextdoor.utils.ResidentUtils;

import java.util.Map;

/**
 * User: David
 * Date: 6/7/13
 * Time: 4:56 PM
 */
public interface Backend {

    public void init();

    public Map<String, Tribe> loadTribes();

    public void saveTribes();

    public Tribe loadTribe(String name);

    public void saveTribe(Tribe tribe);

    public Map<String, Resident> loadResidents(ResidentUtils residentUtils);

    public Resident loadResident(String name, ResidentUtils residentUtils);
    
    public Map<String, Civilisation> loadCivilisations();
    
    public Civilisation loadCivilisation(String name);
    
    public void saveCivilisations();
    
    public void saveCivilisation(Civilisation civ);

}
