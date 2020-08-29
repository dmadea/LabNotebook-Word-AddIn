--drop index indigo_owner.bingo_eln_mol_idx ;
--drop index indigo_owner.bingo_eln_rxn_idx ;

drop tablespace indigo_owner_data including contents and datafiles ;
drop tablespace indigo_owner_lob including contents and datafiles ;

drop role indigo_owner_select_any ;
drop role indigo_owner_update_any ;

drop user indigo_owner cascade ;
drop user indigo_user cascade ;

