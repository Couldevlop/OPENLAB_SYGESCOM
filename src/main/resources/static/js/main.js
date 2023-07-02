/**
 * 
 */
 
 
 $(document).ready(function(){
 
      $('.table .eBtn').on('click', function(event){
         event.preventDefault();
         
        var href = $(this).attr('href');
         $.get(href, function(courrier, status){
            $('.myForm #dateEnvoie).val(courrier.id);
            $('.myForm #agenceEmetrice).val(courrier.agenceEmetrice);
            $('.myForm #numeroOrdre).val(courrier.numeroOrdre);
             $('.myForm #agencesReceptrice).val(courrier.agencesReceptrice);
             $('.myForm #Objectifs).val(courrier.Objectifs);
             $('.myForm #societeCoursiere).val(courrier.societeCoursiere);
             $('.myForm #nomCousier).val(courrier.nomCousier);
             $('.myForm #nbPieces).val(courrier.nbPieces);
             $('.myForm #telCoursier).val(courrier.telCoursier);
         });
        $('.myForm #modalCourrier').modal();
      })
 }); 	 
 
 
 