/*


 */

$('document').ready(function(){
    $('.table .btn').on('click', function(event){
        event.preventDefault();

        var href= $(this).attr('href');

        $.get(href, function(indexeDTO){
            var essenceIndexeFin = $(event.target).closest('tr').find(indexeDTO.essenceIndexeFin);
            var gazoilIndexeFin = $(event.target).closest('tr').find(indexeDTO.gazoilIndexeFin);
            var cuveEssence = $(event.target).closest('tr').find(indexeDTO.cuveEssence);
            var cuveGazoil = $(event.target).closest('tr').find(indexeDTO.cuveGazoil);
            var dateJour = $(event.target).closest('tr').find(indexeDTO.dateJour);

            //make your ajax call populate items or what even you need

            $(this).find('#essenceIndexeFin').text(essenceIndexeFin);
            $(this).find('#gazoilIndexeFin').text(gazoilIndexeFin);
            $(this).find('#cuveEssence').text(cuveEssence);
            $(this).find('#cuveGazoil').text(cuveGazoil);
            $(this).find('#cuveGazoil').text(dateJour);

            /*$('#essenceIndexeFin').val(indexeDTO.essenceIndexeFin);
            $('#gazoilIndexeFin').val(indexeDTO.gazoilIndexeFin);
            $('#cuveEssence').val(indexeDTO.cuveEssence);
            $('#cuveGazoil').val(indexeDTO.cuveGazoil);
            $('#dateJour').val(indexeDTO.dateJour);*/
        });

        $('#modalIndexe').modal();
    });
                             });