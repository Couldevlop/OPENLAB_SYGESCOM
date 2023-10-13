/*  Une vue qui retourne l'état actuel de la station comparé à l'enregistrement de l'histotique
    après une prise d'index */

CREATE VIEW divineoil.controle_depotage as   (
                                             SELECT st.id, h.date_depot,
                                                    h.date_jour,
                                                    h.qte_globale_essence as essence_avant_depot,
                                                    s.qte_globale_essence as essence_apres_depot,
                                                    h.qte_globale_gazoile as gasoil_avant_depot,
                                                    s.qte_globale_gazoile as gasoil_apres_depot,
                                                    st.nom as station
                                             FROM divineoil.doci_historystockstation h
                                                      LEFT JOIN divineoil.doci_stock_station s ON h.stations_id = s.stations_id
                                                      LEFT JOIN divineoil.doci_stations st ON h.stations_id = st.id
                                             where h.date_jour IS NOT null AND s.stations_id=st.id );