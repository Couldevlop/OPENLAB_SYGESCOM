package ci.doci.sygescom.controller;

import ci.doci.sygescom.domaine.*;
import ci.doci.sygescom.exception.BadActionException;
import ci.doci.sygescom.repository.*;
import ci.doci.sygescom.service.OperationsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller

public class ClientsController {
    private final ClientsRepository clientsRepository;
    private final StationsRepository stationsRepository;
    private final ClientsComptantsRepository clientsComptantsRepository;
    private final TypesClientsRepository typesClientsRepository;
    private final ClientCorporateRepository clientCorporateRepository;
    private final BeneficiaireRepository beneficiaireRepository;
    private final CorporateDirecteRepository corporateDirecteRepository;
    private final OperationsService operationsService;

    public ClientsController(ClientsRepository clientsRepository, StationsRepository stationsRepository, ClientsComptantsRepository clientsComptantsRepository, TypesClientsRepository typesClientsRepository, ClientCorporateRepository clientCorporateRepository, BeneficiaireRepository beneficiaireRepository, CorporateDirecteRepository corporateDirecteRepository, OperationsService operationsService) {
        this.clientsRepository = clientsRepository;
        this.stationsRepository = stationsRepository;
        this.clientsComptantsRepository = clientsComptantsRepository;
        this.typesClientsRepository = typesClientsRepository;
        this.clientCorporateRepository = clientCorporateRepository;
        this.beneficiaireRepository = beneficiaireRepository;
        this.corporateDirecteRepository = corporateDirecteRepository;
        this.operationsService = operationsService;
    }

    @GetMapping("/gerant/clients")
    public String clients(Model model) {
        model.addAttribute("client", clientsComptantsRepository.findAll());
        model.addAttribute("clientCorporate", clientCorporateRepository.findAll());
        return "clientsGerant";
    }

    @GetMapping("/gerant/newclient")
    public String newClient(Model model, @AuthenticationPrincipal User user) {
        ClientsDoci client = new ClientsComptants();
        model.addAttribute("client", client);
        model.addAttribute("station", stationsRepository.findAll());
        return "clientsForm";
    }


    @PostMapping("/gerant/newClientComptant")
    public String addClient(@ModelAttribute("client") ClientsComptants clientsComptants,
                            Model model, @AuthenticationPrincipal User user) {
        if(clientsComptants.getTypeClient() == null || clientsComptants.getContact1() == null || clientsComptants.getRemiseEssence() < 0 || clientsComptants.getRemiseGazoil() <0){
            model.addAttribute("message", "Le type de client, contact, les remises sont obligatoire. Pour la remise ou vous n'avez pas de valeurs, mettez 0.0");
            return "errors-action";
        }
        clientsComptantsRepository.save(clientsComptants);
        return "redirect:/gerant/clientsGerant";
    }


    @GetMapping("/gerant/newclientcomptant")
    public String newclient(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("clientsComptantsList", clientsComptantsRepository.findClientsComptantsByStations(user.getStations()));
        model.addAttribute("types", typesClientsRepository.findAll());
        model.addAttribute("clientsComptants", new ClientsComptants());
        model.addAttribute("user", user);
        return "ClientComptant";
    }

    @GetMapping("/gerant/client-comptant/{id}/update")
    public String detailClientComptant(Model model, @PathVariable("id") long id){
        Optional<ClientsComptants> clientsComptants = clientsComptantsRepository.findById(id);
        if(clientsComptants.isPresent()){
            ClientsComptants clicpt = clientsComptants.get();
            String typeClt = clicpt.getTypeClient();
            model.addAttribute("typeclt",typeClt);
            model.addAttribute("clicpt",clicpt);
            return "clientComptant-details";
        }else{
            model.addAttribute("message", "aucun client comptant trouvé");
            return "clientComptant";
        }
    }

    @PostMapping("/gerant/updateClientComptant")
    public String updateClientComptant(@RequestParam("id") long id,
                                       @ModelAttribute ClientsComptants clientsComptants,
                                       @AuthenticationPrincipal User user,
                                       Model model){
        Optional<ClientsComptants> clientsComptants1 =  clientsComptantsRepository.findById(id);
        if(clientsComptants1.isPresent()){
            ClientsComptants cli = clientsComptants1.get();
            cli.setNumPiece(clientsComptants.getNumPiece());
            cli.setTypePiece(clientsComptants.getTypePiece());
            cli.setContact1(clientsComptants.getContact1());
            cli.setRemiseEssence(clientsComptants.getRemiseEssence());
            cli.setRemiseGazoil(clientsComptants.getRemiseGazoil());
            cli.setLocalisation(clientsComptants.getLocalisation());
            cli.setNom(clientsComptants.getNom());
            cli.setEmail(clientsComptants.getEmail());
            cli.setStations(user.getStations());
            cli.setUser(user);
            clientsComptantsRepository.save(cli);
            return "redirect:/gerant/newclientcomptant";
        }
        model.addAttribute("message","Le client est introuvable");
        return "clientCompltant-details";
    }

    @PostMapping("/gerant/addClientComptant")
    public String addClientComptant(@ModelAttribute("client") ClientsComptants clientsComptants,
                                    @RequestParam("nom") String nom,
                                    @RequestParam("contact1") String contact1,
                                    @RequestParam("email") String email,
                                    @RequestParam("typePiece") String typePiece,
                                    @RequestParam("numPiece") String numPiece,
                                    @RequestParam("localisation") String localisation,
                                    Model model, @AuthenticationPrincipal User user) {

        clientsComptants.setDateEnreg(LocalDate.now());
        clientsComptants.setContact1(contact1);
        clientsComptants.setEmail(email);
        clientsComptants.setTypePiece(typePiece);
        clientsComptants.setNom(nom);
        clientsComptants.setLocalisation(localisation);
        clientsComptants.setNumPiece(numPiece);
        clientsComptants.setStations(user.getStations());
        clientsComptantsRepository.save(clientsComptants);
        return "redirect:/gerant/newclientcomptant";
    }

    @GetMapping("/superviseur/clients")
    public String getAllClientCorporate(Model model) {
        ClientsCorporates clientsCorporates = new ClientsCorporates();
        model.addAttribute("cp", clientsCorporates);
        model.addAttribute("stations", stationsRepository.findAll());
        model.addAttribute("clientCorpList", clientCorporateRepository.findClientsCorporatesByTypeClient("SIMPLE"));
        return "clients";
    }

    @GetMapping("/superviseur/clients/corporate/{id}/beneficiares")
    public String addBeneficiaires(Model model, @PathVariable("id") long id) {
        Optional<ClientsCorporates> cli = clientCorporateRepository.findById(id);
        if (cli.isPresent()) {
            ClientsCorporates cliCorp = cli.get();
            model.addAttribute("cli", cliCorp);
            model.addAttribute("beneficiaires", beneficiaireRepository.findBeneficiaireByClientsCorporates(cliCorp));
            model.addAttribute("benef", new Beneficiaire());
            return "listBeneficiaireCorp";
        } else {
            model.addAttribute("message", "Aucun corporate n'est associé au beneficiaire que vous voulez crée");
            return "errors-action";
        }
    }


    @GetMapping("/superviseur/clients/corporate/{id}/update")
    public String shwoCorporate(Model model, @PathVariable("id") long id) {
        Optional<ClientsCorporates> cli = clientCorporateRepository.findById(id);
        if (cli.isPresent()) {
            ClientsCorporates cliCorp = cli.get();
            model.addAttribute("cli", cliCorp);
            model.addAttribute("stations", stationsRepository.findAll());
            //model.addAttribute("benef", new Beneficiaire());
            return "clients-modif";
        } else {
            model.addAttribute("message", "Aucun corporate n'est associé au beneficiaire que vous voulez crée");
            return "errors-action";
        }
    }


    @GetMapping("/superviseur/corporate/beneficiaire/{id}/update")
    public String shwoBenefiaireOfCorporate(Model model, @PathVariable("id") long id) {
        Optional<Beneficiaire> beneCorporate = beneficiaireRepository.findById(id);
        if (beneCorporate.isPresent()) {
            Beneficiaire benef = beneCorporate.get();
            model.addAttribute("beneficiaire", benef);
            model.addAttribute("stations", stationsRepository.findAll());
            //model.addAttribute("benef", new Beneficiaire());
            return "beneficiaireUpdate";
        } else {
            model.addAttribute("message", "Aucun corporate n'est associé au beneficiaire que vous voulez crée");
            return "errors-action";
        }
    }

    @PostMapping("/superviseur/updateCorporate")
    public String updateCorporate(@RequestParam String interlocuteur,
                                  @RequestParam long idClient,
                                  @RequestParam double remiseEssence,
                                  @RequestParam double remiseGazoil,
                                  @RequestParam double plafonageEssence,
                                  @RequestParam double plafonageGazoil,
                                  @RequestParam String email,
                                  @RequestParam String num_doc,
                                  @RequestParam String contact1,
                                  @RequestParam Stations stations,
                                  @RequestParam String nom,
                                  @RequestParam int nbre_beneficiaire,
                                  @AuthenticationPrincipal User user,
                                  Model model){

        Optional<ClientsCorporates> clientsCorporates = clientCorporateRepository.findById(idClient);
        if(clientsCorporates.isPresent()){
            ClientsCorporates clientsCorporates1 = clientsCorporates.get();
            clientsCorporates1.setPlafonageGazoil(plafonageGazoil);
            clientsCorporates1.setPlafonageEssence(plafonageEssence);
            clientsCorporates1.setContact1(contact1);
            clientsCorporates1.setEmail(email);
            clientsCorporates1.setStations(stations);
            clientsCorporates1.setNumDoc(num_doc);
            clientsCorporates1.setInterlocuteur(interlocuteur);
            clientsCorporates1.setUser(user);
            clientsCorporates1.setNbre_beneficiaire(nbre_beneficiaire);
            clientsCorporates1.setLocalisation(nom);
            clientsCorporates1.setDateEnreg(LocalDate.now());
            clientsCorporates1.setRemiseGazoil(remiseGazoil);
            clientCorporateRepository.save(clientsCorporates1);
            return "redirect:/superviseur/clients";
        }
        model.addAttribute("error","Il n'existe pas de client corporate à modifier avec id" + idClient);
        return "clients";

    }

    @GetMapping("/superviseur/newBeneficiaire")
    public String FormBeneficiaire(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("beneficiaire", new Beneficiaire());
        return "beneficiaireForm";

    }


    @PostMapping("/superviseur/newBeneficiaire")
    public String newBeneficiaire(Model model,
                                  @Valid @ModelAttribute("beneficiaire") Beneficiaire beneficiaire,
                                  @AuthenticationPrincipal User user, @RequestParam("id") long id) {

        if (beneficiaire != null && id != 0) {
            ClientsCorporates cp = clientCorporateRepository.findById(id).orElse(null);
            beneficiaire.setClientsCorporates(cp);
            List<Beneficiaire> beneficiaireList=new ArrayList<>();
            beneficiaireList.add(beneficiaire);
            cp.setBeneficiaireList(beneficiaireList);
            beneficiaireRepository.save(beneficiaire);
        } else {
            return "errors-action";
        }
        return "redirect:/superviseur/clients/corporate/" + id;
    }



    @PostMapping("/superviseur/addNewCorporate")
    public String addNewClienCorporate(@ModelAttribute("ClientCorporate") ClientsCorporates cp,
                                       @AuthenticationPrincipal User user,
                                       Model model) {
        if (cp.equals(null)) {
            return "redirect:/superviseur/clienst";
        }
        if(clientCorporateRepository.findClientsCorporatesByContact1(cp.getContact1()) != null){
            model.addAttribute("message", "Un client existant a dèjà ce le numero: " + cp.getContact1());
            return "accessDenied";
        }
        cp.setDateEnreg(LocalDate.now());
        cp.setUser(user);
        clientCorporateRepository.save(cp);
        return "redirect:/superviseur/clients";
    }

    @GetMapping("/superviseur/newClientCorporate")
    public String newClientCorporate(Model model) {
        ClientsDoci clientCoporate = new ClientsCorporates();
        model.addAttribute("ClientCorporate", clientCoporate);
        model.addAttribute("stations", stationsRepository.findAll());
        return "clientCorporateForm";
    }


    @GetMapping("/superviseur/newcliencorporate")
    public String newclientcorporate() {
        return "venteCorporate";
    }

    @GetMapping("/gerant/searchcorporate")
    public String searchClientCorporate(Model model) {
        String corporate = null;
        return "corporateCheck";
    }


}