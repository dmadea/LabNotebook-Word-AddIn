drop index if exists indigo_owner.bingo_eln_rxn_idx ;
drop index if exists indigo_owner.bingo_eln_mol_idx ;

drop owned by indigo_owner cascade ;
drop role if exists indigo_owner ;

drop owned by indigo_user cascade ;
drop role if exists indigo_user ;

commit ;
